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
 * 用户文件表（页面展示）
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public FileUser(long fileId, long originId, int status, Integer fileSize,Integer dirFlag) {
        this.fileId = fileId;
        this.originId = originId;
        this.status = status;
        this.fileSize = fileSize;
        this.dirFlag = dirFlag;
    }

    /**
     * 用户文件id
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 源文件id
     */
    private Long originId;

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
     * 用户id
     */
    private Integer userId;

    /**
     * 父文件夹id(0为顶层目录)
     */
    private Long parentId;

    /**
     * 文件状态（0-正常，1-在回收站，2-已清除）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
