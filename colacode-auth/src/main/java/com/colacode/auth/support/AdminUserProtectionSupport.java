package com.colacode.auth.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.colacode.auth.config.AdminAuthorizationProperties;
import com.colacode.auth.infra.entity.AuthPermission;
import com.colacode.auth.infra.entity.AuthRole;
import com.colacode.auth.infra.entity.AuthRolePermission;
import com.colacode.auth.infra.entity.AuthUserRole;
import com.colacode.auth.infra.mapper.AuthPermissionMapper;
import com.colacode.auth.infra.mapper.AuthRoleMapper;
import com.colacode.auth.infra.mapper.AuthRolePermissionMapper;
import com.colacode.auth.infra.mapper.AuthUserMapper;
import com.colacode.auth.infra.mapper.AuthUserRoleMapper;
import com.colacode.common.enums.ResultCodeEnum;
import com.colacode.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AdminUserProtectionSupport {

    private final AuthUserMapper authUserMapper;
    private final AuthUserRoleMapper authUserRoleMapper;
    private final AuthRoleMapper authRoleMapper;
    private final AuthRolePermissionMapper authRolePermissionMapper;
    private final AuthPermissionMapper authPermissionMapper;
    private final AdminAuthorizationProperties adminAuthorizationProperties;

    public AdminUserProtectionSupport(AuthUserMapper authUserMapper,
                                      AuthUserRoleMapper authUserRoleMapper,
                                      AuthRoleMapper authRoleMapper,
                                      AuthRolePermissionMapper authRolePermissionMapper,
                                      AuthPermissionMapper authPermissionMapper,
                                      AdminAuthorizationProperties adminAuthorizationProperties) {
        this.authUserMapper = authUserMapper;
        this.authUserRoleMapper = authUserRoleMapper;
        this.authRoleMapper = authRoleMapper;
        this.authRolePermissionMapper = authRolePermissionMapper;
        this.authPermissionMapper = authPermissionMapper;
        this.adminAuthorizationProperties = adminAuthorizationProperties;
    }

    public boolean isAdminUser(Long userId) {
        if (userId == null || !hasAdminConfig()) {
            return false;
        }
        return authUserMapper.countAdminUsersByUserId(
                userId,
                roleKeys(),
                permissionKeys(),
                false) > 0;
    }

    public long countAdminUsersExcluding(Long excludeUserId, boolean enabledOnly) {
        if (!hasAdminConfig()) {
            return 0;
        }
        return authUserMapper.countAdminUsers(
                roleKeys(),
                permissionKeys(),
                excludeUserId,
                enabledOnly);
    }

    public boolean willRemainAdminAfterUnassign(Long userId, Long roleId) {
        if (!isAdminUser(userId)) {
            return false;
        }

        AuthRole removingRole = authRoleMapper.selectById(roleId);
        if (removingRole == null || !isAdminRoleKey(removingRole.getRoleKey())) {
            return true;
        }

        for (AuthRole role : listUserRoles(userId)) {
            if (role.getId() != null && role.getId().equals(roleId)) {
                continue;
            }
            if (isAdminRoleKey(role.getRoleKey())) {
                return true;
            }
        }

        return hasAdminPermission(authUserMapper.selectPermissionsByUserId(userId));
    }

    public void assertRoleChangeKeepsEnabledAdmin(Long roleId, String newRoleKey, List<Long> newPermissionIds) {
        AuthRole existingRole = authRoleMapper.selectById(roleId);
        if (existingRole == null) {
            return;
        }

        boolean roleCurrentlyGrantsAdmin = roleGrantsAdmin(existingRole.getRoleKey(), getPermissionIdsByRoleId(roleId));
        boolean roleWillGrantAdmin = roleGrantsAdmin(newRoleKey, newPermissionIds);
        assertEnabledAdminInvariant(roleCurrentlyGrantsAdmin && !roleWillGrantAdmin);
    }

    public void assertRoleDeleteKeepsEnabledAdmin(Long roleId) {
        AuthRole existingRole = authRoleMapper.selectById(roleId);
        if (existingRole == null) {
            return;
        }

        boolean roleCurrentlyGrantsAdmin = roleGrantsAdmin(existingRole.getRoleKey(), getPermissionIdsByRoleId(roleId));
        assertEnabledAdminInvariant(roleCurrentlyGrantsAdmin);
    }

    private void assertEnabledAdminInvariant(boolean removesAdminCapability) {
        if (!hasAdminConfig() || !removesAdminCapability || countAdminUsersExcluding(null, true) > 1) {
            return;
        }
        throw new BusinessException(ResultCodeEnum.LAST_ADMIN_DISABLE_FORBIDDEN);
    }

    private List<AuthRole> listUserRoles(Long userId) {
        LambdaQueryWrapper<AuthUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUserRole::getUserId, userId);
        List<AuthUserRole> userRoles = authUserRoleMapper.selectList(wrapper);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        return userRoles.stream()
                .map(AuthUserRole::getRoleId)
                .filter(roleId -> roleId != null)
                .map(authRoleMapper::selectById)
                .filter(role -> role != null)
                .toList();
    }

    private List<Long> getPermissionIdsByRoleId(Long roleId) {
        LambdaQueryWrapper<AuthRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRolePermission::getRoleId, roleId);
        return authRolePermissionMapper.selectList(wrapper).stream()
                .map(AuthRolePermission::getPermissionId)
                .toList();
    }

    private boolean roleGrantsAdmin(String roleKey, List<Long> permissionIds) {
        return isAdminRoleKey(roleKey) || containsAdminPermission(permissionIds);
    }

    private boolean containsAdminPermission(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty() || permissionKeys().isEmpty()) {
            return false;
        }
        List<AuthPermission> permissions = authPermissionMapper.selectBatchIds(permissionIds);
        return permissions.stream()
                .map(AuthPermission::getPermissionKey)
                .anyMatch(this::isAdminPermissionKey);
    }

    private boolean isAdminRoleKey(String roleKey) {
        return roleKeys().contains(roleKey);
    }

    private boolean hasAdminPermission(List<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return false;
        }
        return permissionKeys.stream().anyMatch(this::isAdminPermissionKey);
    }

    private boolean isAdminPermissionKey(String permissionKey) {
        return permissionKeys().contains(permissionKey);
    }

    private boolean hasAdminConfig() {
        return !roleKeys().isEmpty() || !permissionKeys().isEmpty();
    }

    private List<String> roleKeys() {
        return adminAuthorizationProperties.getRoleKeys() == null
                ? Collections.emptyList()
                : adminAuthorizationProperties.getRoleKeys();
    }

    private List<String> permissionKeys() {
        return adminAuthorizationProperties.getPermissionKeys() == null
                ? Collections.emptyList()
                : adminAuthorizationProperties.getPermissionKeys();
    }
}
