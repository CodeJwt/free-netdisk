package com.jwt.freecloud.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author：揭文滔
 * @since：2023/3/16
 */
@Data
public class FileUserVO implements Serializable {

    private static final long serialVersionUID = 1161057485169266975L;
    /**
     * 用户文件id
     */
    private Long fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件扩展名
     */
    private String fileExtName;

    /**
     * 文件逻辑路径（页面展示的路径）
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 是否为文件夹（0-是，1-不是）
     */
    private Integer dirFlag;

    /**
     * 父文件夹id(0为顶层目录)
     */
    private Long parentId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
