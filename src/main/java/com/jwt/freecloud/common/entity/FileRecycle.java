package com.jwt.freecloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * 回收站表
 *
 * @author jwt
 * @since 2023-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileRecycle implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "recycle_id", type = IdType.AUTO)
    private Long recycleId;

    /**
     * 删除文件的用户id
     */
    private Integer deleteUserId;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 自动清理时间
     */
    private LocalDateTime clearTime;

    /**
     * 自动删除标识（0-未删除（正常展示），1-已自动清理）
     */
    @TableLogic
    private Integer autoFlag;


}
