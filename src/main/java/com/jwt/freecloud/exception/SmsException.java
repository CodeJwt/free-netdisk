package com.jwt.freecloud.exception;

/**
 * @author：揭文滔
 * @since：2023/3/13
 */
public class SmsException extends RuntimeException {
    public SmsException() {super("sms服务调用失败");}

    public SmsException(Throwable cause) {super("sms服务调用失败", cause);}

    public SmsException(String message) {super(message);}

    public SmsException(String message, Throwable cause) {super(message, cause);}
}
