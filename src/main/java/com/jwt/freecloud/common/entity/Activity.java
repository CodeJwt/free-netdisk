package com.jwt.freecloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * 活动表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动id
     */
    @TableId(value = "activity_id", type = IdType.AUTO)
    private Integer activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动url
     */
    private String activityUrl;

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 活动赠送容量
     */
    private Long activityMemory;

    /**
     * 容量有效时间（天）
     */
    private Integer memoryTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动创建时间
     */
    private LocalDateTime createTime;

    /**
     * 活动状态（0-正常，1-下架，2-结束）
     */
    private Integer status;


}
