package com.jwt.freecloud.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author：揭文滔
 * @since：2023/4/1
 */
@Data
public class UploadDTO implements Serializable {

    private static final long serialVersionUID = 5016381061080802941L;

    /**
     * 分片总数
     */
    private Integer totalChunks;

    /**
     * 分片大小
     */
    private Integer chunkSize;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件MD5值
     */
    private String fileIdentify;

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

}
