package com.jwt.freecloud.exception;

/**
 * @author jwt
 * @since 2023/2/4
 */
public class UploadException extends RuntimeException {
    public UploadException(Throwable cause) {
        super("上传出现了异常", cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
