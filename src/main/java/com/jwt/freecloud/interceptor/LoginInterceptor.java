package com.jwt.freecloud.interceptor;

import com.jwt.freecloud.common.constants.UserConstants;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author：揭文滔
 * @since：2023/3/14
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisUtil redisUtil;

    public static ThreadLocal<UserVO> loginUser= new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        //登录、注册等认证操作放行
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/user/auth/**", uri);
        boolean match2 = antPathMatcher.match("/userShare/visit",uri);
        if(match || match2) {
            return true;
        }

        UserVO userVO = (UserVO)session.getAttribute(UserConstants.LOGIN_USER);
        if (userVO == null) {
            return false;
        }
        // springsession 获取到session会自动刷新session过期时间（秒为单位）
        loginUser.set(userVO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //防止ThreadLocal内存泄漏
        loginUser.remove();
    }
}
