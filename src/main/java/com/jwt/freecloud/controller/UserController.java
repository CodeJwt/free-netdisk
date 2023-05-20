package com.jwt.freecloud.controller;


import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.constants.UserConstants;
import com.jwt.freecloud.common.dto.*;
import com.jwt.freecloud.exception.SmsException;
import com.jwt.freecloud.service.UserService;
import com.jwt.freecloud.util.CheckUtil;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 
 * 用户表 前端控制器
 *
 * @author jwt
 * @since 2023-02-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserService userService;

    /**
     * 退出登录，直接注销session
     * @param request 用户请求
     * @return
     */
    @GetMapping("/auth/loginOut")
    public FreeResult loginOut(HttpServletRequest request) {
        request.getSession().invalidate();
        return FreeResult.success();
    }

    /**
     * 返回用户的总容量和已用容量
     * @return
     */
    @GetMapping("/getStorage")
    public FreeResult getStorage() {
        return userService.getStorage();
    }

    /**
     * 重置密码
     * @param forgot
     * @param result
     * @return
     */
    @PostMapping("/auth/forgot/reset")
    public FreeResult forgotPassword(@RequestBody @Valid ForgotPwdDTO forgot, BindingResult result) {
        FreeResult freeResult = CheckUtil.returnErrors(result);
        if (freeResult != null) {
            return freeResult;
        }
        return userService.forgotPassword(forgot);
    }

    /**
     * 忘记密码校验
     * @param check
     * @param result
     * @return
     */
    @PostMapping("/auth/forgot/check")
    public FreeResult forgotCheck(@RequestBody @Valid ForgotCheckDTO check, BindingResult result) {
        FreeResult freeResult = CheckUtil.returnErrors(result);
        if (freeResult != null) {
            return freeResult;
        }
        return userService.forgotCheck(check);
    }

    /**
     * 手机验证码登录
     * @param login
     * @param result
     * @param request 用户请求，用于session登录
     * @return
     */
    @PostMapping("/auth/login/phone")
    public FreeResult loginByPhone(@RequestBody @Valid LoginByPhoneDTO login, BindingResult result, HttpServletRequest request) {
        //if (result.hasErrors()) {
        //    Map<String, String> errors = result.getFieldErrors().stream().collect(
        //            Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        //    return FreeResult.fail().putData(errors);
        //}
        FreeResult freeResult = CheckUtil.returnErrors(result);
        if (freeResult != null) {
            return freeResult;
        }
        return userService.loginByPhone(login, request);
    }

    /**
     * 用户名密码登录
     * @param login
     * @param result
     * @param request 用户请求，用于session登录
     * @return
     */
    @PostMapping("/auth/login/username")
    public FreeResult loginByUsername(@RequestBody @Valid LoginByUsernameDTO login, BindingResult result,HttpServletRequest request) {
        FreeResult freeResult = CheckUtil.returnErrors(result);
        if (freeResult != null) {
            return freeResult;
        }
        return userService.loginByUsername(login, request);
    }

    /**
     * 用户注册
     * @param registerDTO
     * @param result
     * @return
     */
    @PostMapping("/auth/register")
    public FreeResult register(@RequestBody @Valid RegisterDTO registerDTO, BindingResult result) {
        FreeResult freeResult = CheckUtil.returnErrors(result);
        if (freeResult != null) {
            return freeResult;
        }
        return userService.register(registerDTO);
    }

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return
     */
    @GetMapping("/auth/captcha/sms")
    public FreeResult getSmsCaptcha(String phone) {
        if (phone == null) {
            return FreeResult.fail(ResponseMessageEnum.PHONE_EMPTY);
        }
        if (!CheckUtil.isRightPhone(phone)){
            return FreeResult.fail(ResponseMessageEnum.PHONE_FORMAT_ERROR);
        }
        int flag = userService.trySendSmsCaptcha(phone);
        if (flag == UserConstants.SMS_SEND_SUCCESS) {
            //发送成功
            return FreeResult.success(ResponseMessageEnum.CAPTCHA_SEND_SUCCESS);
        } else if (flag == UserConstants.SMS_REQUEST_BAD) {
            //请求过密
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_REQUEST_BAD);
        } else {
            //发送失败异常
            throw new SmsException();
        }

    }

    /**
     * 获取图形验证码
     *
     * @param request 用户请求，获取sessionID
     * @return
     */
    @GetMapping("/auth/captcha/img")
    public FreeResult newImgCaptcha(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String captcha = userService.newImgCaptcha(sessionId);
        return FreeResult.success().putData("img",captcha);
    }


}

