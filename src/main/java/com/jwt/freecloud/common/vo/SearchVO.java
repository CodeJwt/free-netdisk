package com.jwt.freecloud.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author：揭文滔
 * @since：2023/3/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchVO {
    /**
     * 用户文件id
     */
    private Long fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 扩展名
     */
    private String fileExtName;

    /**
     * 是否为文件夹（0-是，1-不是）
     */
    private Integer dirFlag;

    /**
     * 更新时间
     */
    private String updateTime;

}
