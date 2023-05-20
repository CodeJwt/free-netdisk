package com.jwt.freecloud.service.impl;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.constants.UserConstants;
import com.jwt.freecloud.common.dto.*;
import com.jwt.freecloud.common.entity.User;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.dao.UserMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.UserLevelService;
import com.jwt.freecloud.service.UserService;
import com.jwt.freecloud.util.FreeResult;
import com.jwt.freecloud.util.HttpUtils;
import com.jwt.freecloud.util.MinioTemplate;
import com.jwt.freecloud.util.RedisUtil;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 用户表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MinioTemplate minioTemplate;

    @Autowired
    UserLevelService userLevelService;


    /**
     * 更新用户已使用容量，并更新session中的userVO
     * @param userVO
     * @param usedMemory
     */
    @Override
    public void updateUsedMemory(UserVO userVO, Long usedMemory) {
        System.out.println("updateUserUsedMemory");
        Integer userId = userVO.getUserId();
        User user = baseMapper.selectById(userId);
        // 表明是自动清理回收站调用
        if (userVO.getUsername() == null) {
            baseMapper.updateUsedMemory(userId, user.getUsedMemory() - usedMemory);
            return;
        }
        // 获取HttpServletRequest，从而保存内容到session
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        baseMapper.updateUsedMemory(userVO.getUserId(), user.getUsedMemory() + usedMemory);

        userVO.setUsedMemory(usedMemory);
        request.getSession().setAttribute(UserConstants.LOGIN_USER, userVO);
    }

    @Override
    public FreeResult getStorage() {
        UserVO userVO = LoginInterceptor.loginUser.get();
        User user = baseMapper.selectById(userVO.getUserId());
        return FreeResult.success().putData("totalMemory",user.getTotalMemory()).putData("usedMemory",user.getUsedMemory());
    }

    @Override
    public FreeResult forgotCheck(ForgotCheckDTO check) {
        String phone = check.getPhone();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User one = this.getOne(queryWrapper);
        if (one == null) {
            return FreeResult.fail(ResponseMessageEnum.USER_NOT_EXIST);
        }
        Integer flag = checkPhoneCaptcha(phone, check.getCaptcha());
        if (flag == 1) {
            //验证码超时
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_EXPIRE);
        } else if (flag == 2) {
            // 验证码错误
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_ERROR);
        }
        return FreeResult.success();
    }

    @Override
    public FreeResult forgotPassword(ForgotPwdDTO forgot) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", forgot.getPhone());
        User user = baseMapper.selectOne(queryWrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(forgot.getPassword()));
        baseMapper.updateById(user);
        return FreeResult.success(ResponseMessageEnum.PASSWORD_UPDATE_SUCCESS);
    }

    /**
     * 手机号登录
     * @param login
     * @param request
     * @return
     */
    @Override
    public FreeResult loginByPhone(LoginByPhoneDTO login, HttpServletRequest request) {
        String phone = login.getPhone();
        Integer flag = checkPhoneCaptcha(phone, login.getCaptcha());
        if (flag == 1) {
            //验证码超时
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_EXPIRE);
        } else if (flag == 2) {
            // 验证码错误
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return FreeResult.fail(ResponseMessageEnum.USER_NOT_EXIST);
        }
        //TODO 完善uservo内容
        UserVO u = new UserVO();
        BeanUtil.copyProperties(user, u);
        //把uservo存到session中
        request.getSession().setAttribute(UserConstants.LOGIN_USER, u);
        return FreeResult.success(ResponseMessageEnum.LOGIN_SUCCESS).putData("userVO",u);
    }

    /**
     * 验证手机验证码
     * @param phone
     * @param input
     * @return 0-成功，1-验证码超时，2-验证码错误
     */
    public Integer checkPhoneCaptcha(String phone, String input) {
        String[] captcha = getCaptcha(UserConstants.SMS_CAPTCHA_KEY, phone);
        if (captcha == null) {
            //验证码超时
            return 1;
        }
        String code = captcha[0];
        if (!code.equals(input)) {
            return 2;
        }
        return 0;
    }

    /**
     * 用户名密码登录
     * @param login
     * @param request
     * @return
     */
    @Override
    public FreeResult loginByUsername(LoginByUsernameDTO login, HttpServletRequest request) {
        HttpSession session = request.getSession();
        //判断验证码
        String[] captcha = getCaptcha(UserConstants.IMG_CAPTCHA_KEY, session.getId());
        if (captcha == null) {
            //验证码超时
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_EXPIRE);
        }
        String code = captcha[0];
        if (!code.equals(login.getCaptcha())) {
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", login.getUsername());
        User user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            return FreeResult.fail(ResponseMessageEnum.USER_NOT_EXIST);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean flag = encoder.matches(login.getPassword(), user.getPassword());
        if (!flag) {
            return FreeResult.fail(ResponseMessageEnum.USERNAME_PASSWORD_ERROR);
        }
        UserVO u = new UserVO();
        BeanUtil.copyProperties(user, u);
        //把uservo存到session中
        session.setAttribute(UserConstants.LOGIN_USER, u);
        return FreeResult.success(ResponseMessageEnum.LOGIN_SUCCESS).putData("userVO", u);
    }

    /**
     * 注册
     * @param registerDTO
     * @return
     */
    @Transactional
    @Override
    public FreeResult register(RegisterDTO registerDTO) {
        String[] smsCaptcha = getCaptcha(UserConstants.SMS_CAPTCHA_KEY,registerDTO.getPhone());
        if (smsCaptcha != null) {
            //验证码错误
            if (!smsCaptcha[0].equals(registerDTO.getCaptcha())) {

                return FreeResult.fail(ResponseMessageEnum.CAPTCHA_ERROR);
            }
        } else {
            //验证码超时失效
            return FreeResult.fail(ResponseMessageEnum.CAPTCHA_EXPIRE);
        }
        //验证码未失效且验证通过
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", registerDTO.getUsername());
        User u = baseMapper.selectOne(queryWrapper);
        //用户名已存在
        if (u != null) {
            return FreeResult.fail(ResponseMessageEnum.USERNAME_EXIST);
        }
        /**
         * 用户信息保存
         */
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        user.setBaseMemory(UserConstants.USER_BASE_MEMORY);
        user.setTotalMemory(UserConstants.USER_BASE_MEMORY);
        user.setRegTime(now);
        user.setUserId(1);
        int insert = baseMapper.insert(user);

        if (insert <= 0) {
            return FreeResult.fail(ResponseMessageEnum.PHONE_EXIST);
        }

        //用户等级保存
        userLevelService.insertUserLevel(user.getUserId());

        return FreeResult.success(ResponseMessageEnum.REGISTER_SUCCESS);
    }

    @Override
    public int trySendSmsCaptcha(String phone) {
        String[] captcha = getCaptcha(UserConstants.SMS_CAPTCHA_KEY, phone);
        if (captcha != null) {
            // 验证码防刷校验，一分钟内不能重复获取
            String oldTime = captcha[1];
            if (System.currentTimeMillis() - Long.parseLong(oldTime) < 1000 * 60) {
                return UserConstants.SMS_REQUEST_BAD;
            }
        }
        return sendSmsCaptcha(phone);

    }
    public String[] getCaptcha(String prefix, String str) {
        String captcha = redisUtil.getString( prefix + str);
        if (captcha != null) {
            return captcha.split("_");
        }
        return null;
    }
    
    /**
     * 获取短信验证码
     * @param phone
     */
    public int sendSmsCaptcha(String phone) {
        String host = UserConstants.SMS_HOST;
        String path = UserConstants.SMS_PATH;
        String method = UserConstants.SMS_METHOD;
        String appcode = UserConstants.SMS_APPCODE;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String code = IdUtil.randomUUID().substring(0, 4);
        bodys.put("content", "code: "+code);
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        } catch (Exception e) {
            return UserConstants.SMS_SEND_ERROR;
        }

        redisUtil.setStringWithExpire(UserConstants.SMS_CAPTCHA_KEY+phone,
                code + "_"+ System.currentTimeMillis(), UserConstants.SMS_CAPTCHA_EXPIRE, TimeUnit.SECONDS);
        return UserConstants.SMS_SEND_SUCCESS;
    }

    /**
     * 获取图形验证码
     * @param sessionId
     * @return
     */
    @Override
    public String newImgCaptcha(String sessionId) {

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(UserConstants.IMG_CAPTCHA_WIDTH,
                UserConstants.IMG_CAPTCHA_HEIGHT, UserConstants.IMG_CAPTCHA_LENGTH, UserConstants.IMG_CAPTCHA_LINE);

        //lineCaptcha.setGenerator(new RandomGenerator(BASE_CHAR_NUMBER, Const.CAPTCHA_LENGTH));
        lineCaptcha.setFont(new Font("Arial,Courier", Font.BOLD, (int)(UserConstants.IMG_CAPTCHA_HEIGHT * 0.78)));


        redisUtil.setStringWithExpire(UserConstants.IMG_CAPTCHA_KEY + sessionId,
                lineCaptcha.getCode()+"_"+System.currentTimeMillis(), UserConstants.IMG_CAPTCHA_EXPIRE, TimeUnit.SECONDS);


        return lineCaptcha.getImageBase64();
    }
}
