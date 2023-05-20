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
 * 用户传输表
 *
 * @author jwt
 * @since 2023-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer TRANSFER_SUCCESS = 0;

    public static final Integer TRANSFER_FAIL = 1;

    public static final Integer TRANSFER_DOING = 2;

    public static final Integer TRANSFER_DELETED = 3;

    /**
     * 自增id
     */
    @TableId(value = "transfer_id", type = IdType.AUTO)
    private Long transferId;

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
     * 页面展示的路径（对应上传）
     */
    private String filePath;

    /**
     * 本地路径（对应下载）
     */
    private String fileRealPath;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 传输方式（0-上传，1-下载）
     */
    private Integer transferMode;

    /**
     * 传输时间
     */
    private LocalDateTime transferTime;

    /**
     * 传输状态（0-传输中，1-传输失败，2-传输完成，3-已删除记录）
     */
    private Integer status;


}
