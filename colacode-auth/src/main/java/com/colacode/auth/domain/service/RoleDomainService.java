package com.colacode.auth.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.colacode.auth.domain.bo.RoleBO;
import com.colacode.auth.infra.entity.AuthRole;
import com.colacode.auth.infra.entity.AuthRolePermission;
import com.colacode.auth.infra.entity.AuthUserRole;
import com.colacode.auth.infra.mapper.AuthRoleMapper;
import com.colacode.auth.infra.mapper.AuthRolePermissionMapper;
import com.colacode.auth.infra.mapper.AuthUserMapper;
import com.colacode.auth.infra.mapper.AuthUserRoleMapper;
import com.colacode.auth.support.AdminUserProtectionSupport;
import com.colacode.common.enums.ResultCodeEnum;
import com.colacode.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleDomainService {

    private final AuthRoleMapper authRoleMapper;
    private final AuthUserMapper authUserMapper;
    private final AuthUserRoleMapper authUserRoleMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AdminUserProtectionSupport adminUserProtectionSupport;

    public RoleDomainService(AuthRoleMapper authRoleMapper,
                             AuthUserMapper authUserMapper,
                             AuthUserRoleMapper authUserRoleMapper,
                             AuthRolePermissionMapper authRolePermissionMapper,
                             AdminUserProtectionSupport adminUserProtectionSupport) {
        this.authRoleMapper = authRoleMapper;
        this.authUserMapper = authUserMapper;
        this.authUserRoleMapper = authUserRoleMapper;
        this.authRolePermissionMapper = authRolePermissionMapper;
        this.adminUserProtectionSupport = adminUserProtectionSupport;
    }

    public List<RoleBO> getRolesByUserId(Long userId) {
        LambdaQueryWrapper<AuthUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRole::getUserId, userId);
        List<AuthUserRole> userRoles = authUserRoleMapper.selectList(wrapper);

        List<Long> roleIds = userRoles.stream().map(AuthUserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new java.util.ArrayList<RoleBO>();
        }

        List<AuthRole> roles = authRoleMapper.selectBatchIds(roleIds);
        return roles.stream().map(role -> {
            RoleBO bo = new RoleBO();
            bo.setId(role.getId());
            bo.setRoleName(role.getRoleName());
            bo.setRoleKey(role.getRoleKey());

            LambdaQueryWrapper<AuthRolePermission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(AuthRolePermission::getRoleId, role.getId());
            List<AuthRolePermission> rolePerms = authRolePermissionMapper.selectList(permWrapper);
            bo.setPermissionIds(rolePerms.stream().map(AuthRolePermission::getPermissionId).collect(Collectors.toList()));

            return bo;
        }).collect(Collectors.toList());
    }

    public List<RoleBO> listAllRoles() {
        List<AuthRole> roles = authRoleMapper.selectList(null);
        return roles.stream().map(role -> {
            RoleBO bo = new RoleBO();
            bo.setId(role.getId());
            bo.setRoleName(role.getRoleName());
            bo.setRoleKey(role.getRoleKey());

            LambdaQueryWrapper<AuthRolePermission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(AuthRolePermission::getRoleId, role.getId());
            List<AuthRolePermission> rolePerms = authRolePermissionMapper.selectList(permWrapper);
            bo.setPermissionIds(rolePerms.stream().map(AuthRolePermission::getPermissionId).collect(Collectors.toList()));

            return bo;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleBO roleBO) {
        AuthRole role = new AuthRole();
        role.setRoleName(roleBO.getRoleName());
        role.setRoleKey(roleBO.getRoleKey());
        authRoleMapper.insert(role);

        if (roleBO.getPermissionIds() != null) {
            for (Long permId : roleBO.getPermissionIds()) {
                AuthRolePermission rp = new AuthRolePermission();
                rp.setRoleId(role.getId());
                rp.setPermissionId(permId);
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignRoleToUser(Long userId, Long roleId) {
        if (userId == null || authUserMapper.selectById(userId) == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        AuthRole role = authRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND, "角色不存在");
        }

        LambdaQueryWrapper<AuthUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRole::getUserId, userId)
                .eq(AuthUserRole::getRoleId, roleId);
        List<AuthUserRole> existingUserRoles = authUserRoleMapper.selectList(wrapper);
        if (!existingUserRoles.isEmpty()) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST, "用户已分配该角色");
        }

        AuthUserRole userRole = new AuthUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        authUserRoleMapper.insert(userRole);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unassignRoleFromUser(Long userId, Long roleId) {
        if (userId == null || authUserMapper.selectById(userId) == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        AuthRole role = authRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND, "角色不存在");
        }

        LambdaQueryWrapper<AuthUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRole::getUserId, userId)
                .eq(AuthUserRole::getRoleId, roleId);
        List<AuthUserRole> existingUserRoles = authUserRoleMapper.selectList(wrapper);
        if (existingUserRoles.isEmpty()) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND, "用户未分配该角色");
        }

        boolean adminUser = adminUserProtectionSupport.isAdminUser(userId);
        if (adminUser
                && !adminUserProtectionSupport.willRemainAdminAfterUnassign(userId, roleId)
                && adminUserProtectionSupport.countAdminUsersExcluding(userId, true) < 1) {
            throw new BusinessException(ResultCodeEnum.LAST_ADMIN_DISABLE_FORBIDDEN);
        }

        for (AuthUserRole existingUserRole : existingUserRoles) {
            authUserRoleMapper.deleteById(existingUserRole.getId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        LambdaQueryWrapper<AuthRolePermission> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(AuthRolePermission::getRoleId, roleId);
        authRolePermissionMapper.delete(deleteWrapper);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                AuthRolePermission rp = new AuthRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                authRolePermissionMapper.insert(rp);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        adminUserProtectionSupport.assertRoleDeleteKeepsEnabledAdmin(roleId);
        authRoleMapper.deleteById(roleId);

        LambdaQueryWrapper<AuthRolePermission> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.eq(AuthRolePermission::getRoleId, roleId);
        authRolePermissionMapper.delete(permWrapper);

        LambdaQueryWrapper<AuthUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(AuthUserRole::getRoleId, roleId);
        authUserRoleMapper.delete(userRoleWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleBO roleBO) {
        adminUserProtectionSupport.assertRoleChangeKeepsEnabledAdmin(roleBO.getId(), roleBO.getRoleKey(), roleBO.getPermissionIds());
        AuthRole role = new AuthRole();
        role.setId(roleBO.getId());
        role.setRoleName(roleBO.getRoleName());
        role.setRoleKey(roleBO.getRoleKey());
        authRoleMapper.updateById(role);

        if (roleBO.getPermissionIds() != null) {
            assignPermissionsToRole(roleBO.getId(), roleBO.getPermissionIds());
        }
    }
}
