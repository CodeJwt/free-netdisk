package com.jwt.freecloud.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author：揭文滔
 * @since：2023/4/19
 */
@Data
public class MyShareVO implements Serializable {
    private static final long serialVersionUID = -8692741330110424629L;

    /**
     * 分享记录id
     */
    private Long shareId;

    /**
     * 是否公开（公开不需要密码）0-公开，1-不公开
     */
    private Integer publicFlag;

    /**
     * 前端展示用：固定为文件夹
     */

    private Integer dirFlag = 0;

    /**
     * 分享时间
     */
    private LocalDateTime shareTime;

    /**
     * 过期时间
     */
    private LocalDateTime endTime;

    /**
     * 是否过期
     */
    public String overdue;



}
