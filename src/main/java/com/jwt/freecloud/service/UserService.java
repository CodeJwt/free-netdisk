package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.dto.*;
import com.jwt.freecloud.common.entity.User;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.util.FreeResult;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 用户表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface UserService extends IService<User> {

    /**
     * 获取图形验证码
     * @param sessionId
     * @return
     */
    String newImgCaptcha(String sessionId);

    /**
     * 尝试获取验证码
     * @param phone
     * @return
     */
    int trySendSmsCaptcha(String phone);

    FreeResult register(RegisterDTO registerDTO);

    FreeResult loginByUsername(LoginByUsernameDTO login, HttpServletRequest request);

    FreeResult loginByPhone(LoginByPhoneDTO login, HttpServletRequest request);

    /**
     * 更新用户已使用容量，并更新session中的userVO
     * @param userVO
     * @param newUsedMemory
     */
    void updateUsedMemory(UserVO userVO,Long newUsedMemory);

    /**
     * 忘记密码
     * @param login
     * @return
     */
    FreeResult forgotPassword(ForgotPwdDTO login);

    /**
     * 忘记密码重置前校验
     * @param check
     * @return
     */
    FreeResult forgotCheck(ForgotCheckDTO check);

    FreeResult getStorage();
}
