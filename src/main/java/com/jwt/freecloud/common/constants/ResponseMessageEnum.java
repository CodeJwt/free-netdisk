package com.jwt.freecloud.common.constants;

/**
 * 响应消息
 * @author jwt
 * @since 2023/2/4
 */

public enum ResponseMessageEnum {

    REQUEST_SECCESS(200, "请求成功"),

    FILE_UPLOAD_SUCCESS(200,"文件上传成功"),

    FILE_SECOND_UPLOAD_SUCCESS(200, "文件秒传成功"),

    FILE_UPLOAD_CREATED_SUCCESS(200,"文件上传任务创建成功"),

    FILE_UPLOAD_FAIL(50001,"文件上传失败，请重试或重新上传"),

    FILE_SHARE_SUCCESS(200,"分享成功"),

    FILE_SHARE_DELETED(50002,"分享的文件已被删除"),

    FILE_SHARE_EXPIRE(50003,"分享已过期"),

    FILE_NAME_ILLEGAL(50004,"文件名不能包含以下字符：<,>,|,*,?,/"),

    FILE_MOVE_SUCCESS(200,"移动成功"),
    FILE_MOVE_ERROR(50005,"移动失败，请先剪切文件"),

    FOLDER_CREATE_ERROR(50006,"新建文件夹失败"),

    SPACE_NOT_ENOUGH(50007,"剩余空间不足"),

    FILE_PASTE_SUCCESS(200,"粘贴成功"),
    FILE_PASTE_EMPTY(50008,"粘贴失败，请先复制文件"),

    FILE_NOT_EXIST(50009,"文件不存在"),

    FILE_RENAME_SUCCESS(200,"重命名成功"),
    FILE_RENAME_ERROR(50010,"文件名重复"),

    FILENAME_EMPTY_ERROR(50011,"文件(夹)名不能为空"),

    FILE_DELETE_SUCCESS(200,"删除成功"),
    FILE_DELETE_ERROR(50012,"删除失败"),

    CAPTCHA_ERROR(50101,"验证码错误"),
    CAPTCHA_EXPIRE(50102,"验证码已失效"),
    CAPTCHA_REQUEST_BAD(50103,"验证码请求频繁，请稍后重试"),
    CAPTCHA_SEND_ERROR(50104,"验证码发送失败，请重试"),
    CAPTCHA_SEND_SUCCESS(200,"验证码已发送，请注意查收"),

    LOGIN_SUCCESS(200,"登录成功"),

    USER_UPDATE_SUCCESS(200,"用户信息更新成功"),
    USER_UPDATE_ERROR(50106,"用户信息更新失败"),
    PASSWORD_UPDATE_SUCCESS(200,"密码修改成功"),
    PASSWORD_UPDATE_ERROR(50107,"密码修改失败"),

    PASSWORD_RESET_SUCCESS(200,"重置密码成功"),
    PASSWORD_RESET_ERROR(50108,"重置密码失败"),

    REGISTER_SUCCESS(200,"注册成功"),
    REGISTER_ERROR(50109,"注册失败"),

    USERNAME_PASSWORD_ERROR(50110,"用户名或密码错误"),
    USERNAME_EMPTY(50111,"用户名不能为空"),
    USERNAME_ILLEGAL(50112,"用户名为长度为6-16位的英文/数字/下划线"),
    USER_NOT_EXIST(50113,"用户不存在"),
    USERNAME_EXIST(50114,"用户名已存在，请更换"),

    PASSWORD_EMPTY(50115,"密码不能为空"),
    PASSWORD_ILLEGAL(50116,"密码长度为6-16位"),

    NICKNAME_EMPTY(50117,"请输入用户昵称"),
    NICKNAME_ILLEGAL(50118,"用户昵称为长度为1-12位的中英文/数字/下划线"),

    OLD_PASSWORD_ERROR(50119,"旧密码不正确"),

    PHONE_EXIST(50120,"手机号已被注册"),
    PHONE_EMPTY(50121,"手机号不能为空"),
    PHONE_FORMAT_ERROR(50122,"手机号格式错误"),

    FILE_RECOVER_SUCCESS(200,"文件还原成功"),
    FILE_RECOVER_ERROR(50123,"文件还原失败"),

    FILE_DOWNLOAD_ERROR(50130, "文件下载失败");

    private Integer code;
    private String message;
    
    ResponseMessageEnum(Integer code, String message) {
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
