package com.jwt.freecloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * 用户分享详情表
 *
 * @author jwt
 * @since 2023-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserShareItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享细分项id
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    /**
     * 分享记录id
     */
    private Long shareId;

    /**
     * 关联文件id（user_file）
     */
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
     * 是否为文件夹（0-是，1-不是）
     */
    private Integer dirFlag;

    /**
     * 文件大小
     */
    private Integer fileSize;


}
