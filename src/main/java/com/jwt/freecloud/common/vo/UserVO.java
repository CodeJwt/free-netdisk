package com.jwt.freecloud.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author：揭文滔
 * @since：2023/3/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {

    private static final long serialVersionUID = -5327105645791779346L;

    private Integer userId;

    /**
     * 用户名(账号）
     */
    private String username;


    /**
     * 用户昵称
     */
    private String nickname;


    /**
     * 注册时间
     */
    private LocalDateTime regTime;

    /**
     * 网盘总容量
     */
    private Long totalMemory;

    /**
     * 网盘已用容量
     */
    private Long usedMemory;

    /**
     * 用户等级
     */
    private Integer level;

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
