package com.colacode.auth.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "角色ID不能为空")
    private Long id;
}
