package com.jwt.freecloud.util;

import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jwt
 * @since 2023/2/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FreeResult implements Serializable {

    private static final long serialVersionUID = 1233136025242956983L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private Map<String, Object> data;

    /**
     * 请求成功
     * @return
     */
    public static FreeResult success() {
        FreeResult success = new FreeResult();
        success.data = new HashMap<>();
        success.setCode(ResponseMessageEnum.REQUEST_SECCESS.getCode());
        success.setMessage(ResponseMessageEnum.REQUEST_SECCESS.getMessage());
        return success;
    }

    /**
     * 请求成功
     * @param rme 成功信息
     * @return
     */
    public static FreeResult success(ResponseMessageEnum rme) {
        FreeResult success = new FreeResult();
        success.data = new HashMap<>();
        success.setCode(rme.getCode());
        success.setMessage(rme.getMessage());
        return success;
    }

    public static FreeResult fail() {
        FreeResult fail = new FreeResult();
        fail.setCode(RequestExceptionEnum.UNKNOWN_ERROR.getCode());
        fail.setMessage(RequestExceptionEnum.UNKNOWN_ERROR.getMessage());
        return fail;
    }

    /**
     * 请求异常
     * @param ree 异常信息
     * @return
     */
    public static FreeResult fail(RequestExceptionEnum ree) {
        FreeResult fail = new FreeResult();
        fail.setCode(ree.getCode());
        fail.setMessage(ree.getMessage());
        return fail;
    }

    /**
     * 请求失败
     * @param rme 失败信息
     * @return
     */
    public static FreeResult fail(ResponseMessageEnum rme) {
        FreeResult fail = new FreeResult();
        fail.setCode(rme.getCode());
        fail.setMessage(rme.getMessage());
        return fail;
    }

    public FreeResult setResultMap(Map<String, Object> map) {
        this.data = map;
        return this;
    }

    public FreeResult putData(String keyName, Object param) {
        this.data.put(keyName, param);
        return this;
    }

    public FreeResult putMessage(String message) {
        this.setMessage(message);
        return this;
    }
}
