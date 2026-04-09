package com.colacode.auth.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class UnassignRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}
