package com.jwt.freecloud.common.dto;

import lombok.Data;

/**
 * @author：揭文滔
 * @since：2023/4/2
 */
@Data
public class ShareDTO {

    /**
     * 是否公开（公开不需要密码）0-公开，1-不公开
     */
    private Integer publicFlag;

    /**
     * 持续时间（天数）
     */
    private Integer endTime;
}
