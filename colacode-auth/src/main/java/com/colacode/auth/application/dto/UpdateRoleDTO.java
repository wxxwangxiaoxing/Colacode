package com.colacode.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "角色ID不能为空")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    private List<Long> permissionIds;
}
