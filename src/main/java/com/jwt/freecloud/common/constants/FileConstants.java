package com.jwt.freecloud.common.constants;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * @author jwt
 * @since 2023/2/4
 */
public class FileConstants {

    /**
     * 文件秒传MD5存储前缀
     */
    public static final String FILE_FAST_UPLOAD_PREFIX = "file_fast_upload:";

    /**
     * 用户最大上传文件大小
     */
    public static final int FILE_MAX_UPLOAD = Integer.MAX_VALUE;

    /**
     * 最大分片数（最大上传文件大小/分片大小（10M））
     */
    public static final int MAX_TOTAL_CHUNKS = 200;

    /**
     * 单个文件上传经验值
     */
    public static final int UPLOAD_EXP = 50;

    /**
     * 文件秒传md5过期天数
     */
    public static final long FILE_SECEND_IDENTIFY_EXPIRE = 7L;

    /**
     * 文件传输content-type
     */
    public static final String FILE_UPLOAD_CONTENT_TYPE = "application/octet-stream";

    /**
     * 文件上传临时保留信息目录
     */
    public static final String FILE_UPLOAD_INFO_PREFIX = "file_upload_info:";

    /**
     * 用户文件复制临时保存目录
     */
    public static final String FILE_COPY_PREFIX = "file_copy:";

    /**
     * 用户文件复制临时保存目录过期时间
     */
    public static final long FILE_COPY_EXPIRE = 600;

    public static final int LIST_BY_PARENTID = 1;

    public static final int LIST_BY_PATH = 2;




    /**
     * 文件移动操作
     */
    public static final String FILE_MOVE = "move";

    /**
     * 文件粘贴操作
     */
    public static final String FILE_PASTE = "paste";

    public static final int SHARE_PUBLIC = 0;

    public static final int SHARE_SECRET = 1;

    public static final int SHARE_FOREVER = 0;

    public static final int SHARE_TEMP = 1;

    /**
     * 过期时间
     */
    public static final int SHARE_FOREVER_TIME = 99;

    public static final LocalDateTime FORREVER_TIME = LocalDateTime.of(2099, Month.DECEMBER,30,23,59,59);

    /**
     * 回收站自动清理时间(24 * 3600 * 1000)
     */
    public static final long FILE_AUTO_CLEAN_TIME = 20 * 1000;

    /**
     * 回收交换机
     */
    public static final String RECYCLE_EXCHANGE = "recycle-exchange";

    /**
     * 回收延时队列路由键
     */
    public static final String RECYCLE_CLEAN_DELAY = "recycleCleanDelay";

    /**
     * 回收清理路由键
     */
    public static final String RECYCLE_CLEAN = "recycleClean";

    /**
     * 文件夹移动路由键
     */
    public static final String DIRECTORY_MOVE = "directoryMove";

    /**
     * 文件夹标识
     */
    public static final int DIRECTORY_FLAG = 0;

    public static final long DIRECTORY_ORIGINID = -1L;

    /**
     * 文件标识
     */
    public static final int FILE_FLAG = 1;

    /**
     * 文件正常
     */
    public static final int FILE_NORMAL = 0;

    /**
     * 文件被回收
     */
    public static final int FILE_RECYCLE = 1;

    /**
     * 文件已删除
     */
    public static final int FILE_DELETE = 2;

    /**
     * 是文件夹
     */
    public static final int IS_FOLDER = 0;

    /**
     * 是文件
     */
    public static final int IS_FILE = 1;

    /**
     * 上传状态
     */
    public static final int FILE_UPLOAD = 0;

    /**
     * 下载状态
     */
    public static final int FILE_DOWNLOAD = 1;

    /**
     * 传输中
     */
    public static final int TRANSFER_ING = 0;

    /**
     * 传输失败
     */
    public static final int TRANSFER_ERROR = 1;

    /**
     * 传输成功
     */
    public static final int TRANSFER_SUCCESS = 2;

    /**
     * 传输记录已删除
     */
    public static final int TRANSFER_DELETED = 3;
}
