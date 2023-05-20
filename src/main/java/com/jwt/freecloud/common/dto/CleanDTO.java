package com.jwt.freecloud.common.dto;

import lombok.Data;

/**
 * @author：揭文滔
 * @since：2023/4/18
 */
@Data
public class CleanDTO {

    /**
     * 回收细分项id
     */
    private Long itemId;

    /**
     * 关联文件id（user_file）
     */
    private Long fileId;

    /**
     * 文件大小
     */
    private Integer fileSize;
}
