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
 * 用户参与活动记录表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserJoin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户参与记录id
     */
    @TableId(value = "join_id", type = IdType.AUTO)
    private Integer joinId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 关联活动id
     */
    private Integer activityId;

    /**
     * 参与活动时间
     */
    private LocalDateTime joinTime;

    /**
     * 活动赠送容量
     */
    private Long activityMemory;

    /**
     * 容量有效时间（天）
     */
    private Integer memoryTime;


}
