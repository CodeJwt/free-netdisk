package com.jwt.freecloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * 回收站详细表
 *
 * @author jwt
 * @since 2023-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileRecycleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回收细分项id
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    /**
     * 回收记录id
     */
    private Long recycleId;

    /**
     * 关联文件id（user_file）
     */
    private Long fileId;

    /**
     * 源文件id
     */
    private Long originId;

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
    @TableLogic
    private Integer status;


}
