package com.jwt.freecloud.exception;

/**
 * @author jwt
 * @since 2023/2/4
 */
public class NotLoginException extends RuntimeException {
    public NotLoginException() {
        super("未登录");
    }

    public NotLoginException(Throwable cause) {
        super("未登录", cause);
    }

    public NotLoginException(String message) {
        super(message);
    }

    public NotLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
