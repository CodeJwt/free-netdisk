package com.jwt.freecloud.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author：揭文滔
 * @since：2023/3/13
 */
@Data
public class RegisterDTO {

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
     * 用户昵称
     */
    @NotEmpty(message = "昵称不能为空")
    private String nickname;

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String phone;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String captcha;
}
