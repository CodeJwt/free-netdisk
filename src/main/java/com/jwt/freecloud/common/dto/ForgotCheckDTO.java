package com.jwt.freecloud.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author：揭文滔
 * @since：2023/4/11
 */
@Data
public class ForgotCheckDTO {
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
