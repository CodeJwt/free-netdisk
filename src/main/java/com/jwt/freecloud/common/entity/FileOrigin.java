package com.jwt.freecloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * 源文件表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FileOrigin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 源文件id
     */
    @TableId(value = "origin_id", type = IdType.AUTO)
    private Long originId;

    /**
     * 文件md5标识（存于minio的对象名）
     */
    private String fileIdentify;

    /**
     * 文件预览url
     */
    private String previewUrl;


    /**
     * 文件扩展名
     */
    private String fileExtName;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件类型（0-文本，1-图片，2-视频，3-音乐，4-其它）
     */
    private Integer fileType;

    /**
     * 文件状态（0-正常，1-已清除）
     */
    @TableLogic
    private Integer status;

    /**
     * 最初上传的用户id
     */
    private Integer createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
