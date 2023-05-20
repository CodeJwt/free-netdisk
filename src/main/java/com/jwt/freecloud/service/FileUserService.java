package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.dto.ListFilesDTO;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.vo.FileUserVO;
import com.jwt.freecloud.util.FreeResult;

import java.io.IOException;
import java.util.List;

/**
 * 
 * 用户文件表（页面展示） 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface FileUserService extends IService<FileUser> {

    Long getParentIdByPath(String path, Integer userId);
    /**
     * 列出当前文件夹下的所有文件
     * @param listFilesDTO
     * @return
     */
    FreeResult listFiles(ListFilesDTO listFilesDTO, Integer method);

    /**
     * 批量删除文件到回收站，用户文件逻辑删除（文件状态置为回收站）
     * @param fileIds
     * @return
     */
    FreeResult filesToRecycle(List<Long> fileIds) throws IOException;


    /**
     * 批量删除文件（逻辑删除，文件状态置为已删除）
     * @param fileIds
     */
    List<FileUser> deleteBatchByIds(List<Long> fileIds);


    FreeResult renameFile(FileUserVO fileUserVO) throws IOException;

    FreeResult createFolder(String filePath,String fileName, Long parentId);

    FreeResult copyFiles(List<Long> fileIds, String method);

    FreeResult pasteFiles(String newPath) throws IOException;

    FreeResult copyDirectory(Long directoryId, String method);
    
    FreeResult pasteDirectory(String newPath) throws IOException;

    void recoverFiles(List<Long> fileIds);

}
