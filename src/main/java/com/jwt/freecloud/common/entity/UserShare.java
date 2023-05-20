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
 * 用户分享表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserShare implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享记录id
     */
    @TableId(value = "share_id", type = IdType.AUTO)
    private Long shareId;

    /**
     * 分享唯一标识uuid）
     */
    private String shareIdentify;

    /**
     * 分享文件的用户id
     */
    private Integer shareUserId;

    /**
     * 提取码（4位）
     */
    private String sharePwd;

    /**
     * 是否公开（公开不需要密码）0-公开，1-不公开
     */
    private Integer publicFlag;

    /**
     * 分享时间
     */
    private LocalDateTime shareTime;

    /**
     * 过期时间
     */
    private LocalDateTime endTime;

    /**
     * 分享状态（0-正常，1-已失效）
     */
    private Integer status;

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 保存次数
     */
    private Integer saveCount;


}
