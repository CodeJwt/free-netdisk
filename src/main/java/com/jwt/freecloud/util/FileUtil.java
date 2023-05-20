package com.jwt.freecloud.util;

import com.jwt.freecloud.service.FileUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author：揭文滔
 * @since：2023/4/1
 */
public class FileUtil {

    @Autowired
    static FileUserService fileUserService;
    /**
     * 判断文件类型，将数字储存到数据库
     * @param fileExtName
     * @return
     */
    public static Integer getFileType(String fileExtName) {
        switch (fileExtName) {
            // 文档
            case "txt":
                return 1;
            // 图片
            case "png":
                return 2;
            case "jpg":
                return 2;
            case "jpeg":
                return 2;
            // 音频
            case "mp3":
                return 3;
            case "ape":
                return 3;
            case "flac":
                return 3;
            // 视频
            case "mp4":
                return 4;
            case "mkv":
                return 4;
            case "avi":
                return 4;
            case "flv":
                return 4;
            // 压缩包
            case "zip":
                return 5;
            case "rar":
                return 5;
            case "7z":
                return 5;
            case "jar":
                return 5;
            // 安卓安装包
            case "apk":
                return 6;
            // word
            case "doc":
                return 7;
            case "docx":
                return 7;
            //excel
            case "xls":
                return 8;
            case "xlsx":
                return 8;
            // ppt
            case "ppt":
                return 9;
            case "pptx":
                return 9;
            // pdf
            case "pdf":
                return 10;
            // 其他
            default:
                return 20;
        }
    }

    /**
     * 返回排序字段
     * @param order
     * @return
     */
    public static String getFileOrder(String order) {
        if ("updateTime".equals(order)) {
            return "update_time";
        } else if ("fileSize".equals(order)) {
            return "file_size";
        }
        return null;
    }

    public static String getVoPath(String path) {
        int i = path.lastIndexOf('/');
        String voPath = path.substring(0, i + 1);
        return voPath;
    }
}
