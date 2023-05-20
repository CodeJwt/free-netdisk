package com.jwt.freecloud.common.constants;

/**
 * @author jwt
 * @since 2023/2/4
 */
public enum RequestExceptionEnum {

    UNKNOWN_ERROR( 999999, "未知错误"),
    DAO_INSERT_ERROR( 100000, "插入数据异常"),
    DAO_SELECT_ERROR( 100001, "查询数据异常"),
    DAO_UPDATE_ERROR( 100002, "更新数据异常"),
    DAO_DELETE_ERROR( 100003, "删除数据异常"),
    NULL_POINT( 100004, "空指针异常"),
    INDEX_OUT_OF_BOUNDS( 100005, "下标越界异常"),
    REQUEST_TIMEOUT( 100006, "请求超时"),
    PARAM_ERROR( 100007, "参数错误"),
    NOT_INIT_DATA( 100008, "数据未初始化"),
    CUSTOM_ERROR( 200000, "自定义错误"),
    USER_FORBIDDEN( 200001, "用户被禁用"),
    BAD_REQUEST(300000, "非法访问"),
    NOT_LOGIN_ERROR( 200002, "未登录");

    private Integer code;
    private String message;

    RequestExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
