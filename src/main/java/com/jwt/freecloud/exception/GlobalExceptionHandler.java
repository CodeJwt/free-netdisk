package com.jwt.freecloud.exception;

import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.util.FreeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局统一异常处理
 * @author jwt
 * @since 2023/2/4
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FreeResult error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return FreeResult.fail();    // 通用异常结果
    }

    /**-------- 自定义上传异常处理方法 --------**/
    @ExceptionHandler(UploadException.class)
    @ResponseBody
    public FreeResult uploadException(UploadException e) {
        log.error("上传文件异常捕获：" + e);
        return FreeResult.fail(RequestExceptionEnum.REQUEST_TIMEOUT);
    }

    /**-------- 空指针异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FreeResult nullPointerException(NullPointerException e) {
        log.error("空指针异常捕获：{}",e.getMessage(),e);
        return FreeResult.fail(RequestExceptionEnum.NULL_POINT);
    }



    /**-------- 下标越界处理方法 --------**/
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FreeResult indexOutOfBoundsException(IndexOutOfBoundsException e) {
        log.error("下标越界异常捕获：" + e);
        return FreeResult.fail(RequestExceptionEnum.INDEX_OUT_OF_BOUNDS);
    }



    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    //@ResponseStatus(HttpStatus.UNAUTHORIZED)
    public FreeResult notLoginException(NotLoginException e) {
        log.error("登录异常捕获：" + e);
        return FreeResult.fail(RequestExceptionEnum.NOT_LOGIN_ERROR);
    }

    /**
     * 方法参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    public FreeResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return FreeResult.fail(RequestExceptionEnum.PARAM_ERROR).putMessage(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(SmsException.class)
    @ResponseBody
    public FreeResult smsException(SmsException e) {
        log.error("sms服务异常："+ e);
        return FreeResult.fail(ResponseMessageEnum.CAPTCHA_SEND_ERROR);
    }
}
