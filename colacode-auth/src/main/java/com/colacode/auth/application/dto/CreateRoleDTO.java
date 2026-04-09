package com.colacode.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    private List<Long> permissionIds;
}
