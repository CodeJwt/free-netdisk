package com.jwt.freecloud.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author：揭文滔
 * @since：2023/3/14
 */
@Data
public class LoginByUsernameDTO {
    /**
     * 用户名(账号）
     */
    @NotEmpty(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String captcha;
}
