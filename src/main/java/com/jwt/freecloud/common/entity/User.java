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
 * 用户表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名(账号）
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 性别（0-女，1-男）
     */
    private Integer sex;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 注册时间
     */
    private LocalDateTime regTime;

    /**
     * 网盘总容量
     */
    private Long totalMemory;

    /**
     * 基础容量（随等级变化）
     */
    private Long baseMemory;

    /**
     * 参与活动获得容量
     */
    private Long activitiesMemory;

    /**
     * 付费获得容量
     */
    private Long payMemory;

    /**
     * 网盘已用容量
     */
    private Long usedMemory;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（0-正常，1-冻结，2-封禁）
     */
    private Integer userStatus;

    /**
     * 头像url
     */
    private String profilePhoto;


}
