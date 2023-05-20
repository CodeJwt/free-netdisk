package com.jwt.freecloud.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author：揭文滔
 * @since：2023/4/3
 */
@Data
public class ForgotPwdDTO {
    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;
}
