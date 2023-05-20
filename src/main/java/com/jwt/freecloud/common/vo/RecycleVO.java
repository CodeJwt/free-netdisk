package com.jwt.freecloud.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author：揭文滔
 * @since：2023/4/18
 */
@Data
public class RecycleVO {
    /**
     * 自增id
     */
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
     * 回收细分项id
     */
    private Long itemId;

    /**
     * 关联文件id（user_file）
     */
    private Long fileId;

    /**
     * 是否为文件夹（0-是，1-不是）
     */
    private Integer dirFlag;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件扩展名
     */
    private String fileExtName;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件状态（0-正常，1-已恢复或彻底删除）
     */
    private Integer status;
}
