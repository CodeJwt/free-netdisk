package com.jwt.freecloud.common.constants;

/**
 * 用户常量
 * @author jwt
 * @since 2023/2/4
 */
public class UserConstants {

    /**
     * 用户正常
     */
    public static final int USER_NORMAL = 0;

    /**
     * 用户冻结
     */
    public static final int USER_FROZEN = 1;

    /**
     * 用户封禁
     */
    public static final int USER_BAN = 2;

    /**
     * 用户名最小长度
     */
    public static final int USERNAME_MIN_LENGTH = 1;

    /**
     * 用户名最大长度
     */
    public static final int USERNAME_MAX_LENGTH = 12;

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 16;

    /**
     * 用户session前缀
     */
    public static final String LOGIN_USER = "loginUser";

    /**
     * 用户session过期时间
     */
    public static final Long LOGIN_SESSSION_EXPIRE = 1800L;

    /**
     * 注册用户初始等级
     */
    public static final int USER_BASE_LEVEL = 1;


    public static final Long USER_BASE_MEMORY = 1024 * 1024 * 1024 * 5L;

    /**
     * 用户升级exp（等级*该数值）
     */
    public static final int USER_UPGRADE_EXP = 500;

    /**
     * 短信验证码发送成功
     */
    public static final int SMS_SEND_SUCCESS = 0;

    /**
     * 短信验证码请求过密
     */
    public static final int SMS_REQUEST_BAD = 1;

    /**
     * 短信验证码发送失败
     */
    public static final int SMS_SEND_ERROR = 2;

    /**
     * 短信验证码前缀
     */
    public static final String SMS_CAPTCHA_KEY = "free_phone_captcha:";

    /**
     * 短信验证码过期时间（600s）
     */
    public static final long SMS_CAPTCHA_EXPIRE = 600;

    /**
     * 短信验证码请求域名
     */
    public static final String SMS_HOST = "https://dfsns.market.alicloudapi.com";

    /**
     * 短信验证码请求方式
     */
    public static final String SMS_METHOD = "POST";

    /**
     * 阿里云第三方验证appcode
     */
    public static final String SMS_APPCODE = "8ed394fc845646a99cbb4f5f92c0c1ba";


    /**
     * 短信验证码请求路径
     */
    public static final String SMS_PATH = "/data/send_sms";

    /**
     * 图形验证码前缀
     */
    public static final String IMG_CAPTCHA_KEY = "free_img_captcha:";

    /**
     * 图形验证码过期时间（120s）
     */
    public static final long IMG_CAPTCHA_EXPIRE = 120;

    /**
     * 图形验证码宽度
     */
    public static final int IMG_CAPTCHA_WIDTH = 100;

    /**
     * 图形验证码高度
     */
    public static final int IMG_CAPTCHA_HEIGHT = 34;

    /**
     * 图形验证码长度
     */
    public static final int IMG_CAPTCHA_LENGTH = 4;

    /**
     * 图形验证码干扰线数目
     */
    public static final int IMG_CAPTCHA_LINE = 150;


}
