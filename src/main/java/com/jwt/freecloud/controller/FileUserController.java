package com.jwt.freecloud.controller;


import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.dto.ListFilesDTO;
import com.jwt.freecloud.common.vo.FileUserVO;
import com.jwt.freecloud.service.FileUserService;
import com.jwt.freecloud.util.FreeResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 * 用户文件表（页面展示） 前端控制器
 *
 * @author jwt
 * @since 2023-02-08
 */
@RestController
@RequestMapping("/fileUser")
public class FileUserController {

    @Autowired
    public FileUserService fileUserService;

    @GetMapping("/bread")
    public FreeResult listFilesByBreadCrumb(ListFilesDTO listFilesDTO) {
        return fileUserService.listFiles(listFilesDTO, FileConstants.LIST_BY_PATH);
    }

    @GetMapping("/list")
    public FreeResult listFilesByParentId(ListFilesDTO listFilesDTO) {
        return fileUserService.listFiles(listFilesDTO, FileConstants.LIST_BY_PARENTID);
    }

    @SneakyThrows
    @PostMapping("/toRecycle")
    public FreeResult deleteFiles(@RequestParam("fileIds") List<Long> fileIds) {
        return fileUserService.filesToRecycle(fileIds);
    }

    @SneakyThrows
    @PostMapping("/rename")
    public FreeResult renameFile(@RequestBody FileUserVO fileUserVO) {
        return fileUserService.renameFile(fileUserVO);
    }


    /**
     * 新建文件夹
     * @param filePath
     * @param fileName
     * @return
     */
    @GetMapping("/createFolder")
    public FreeResult createFolder(String filePath, String fileName, Long parentId){
        return fileUserService.createFolder(filePath, fileName, parentId);
    }
    

    /**
     * 批量文件复制（剪切、复制操作）
     * @return
     */
    @PostMapping("/copy/files")
    public FreeResult copyFiles(@RequestParam("fileIds") List<Long> fileIds, String method) {
        return fileUserService.copyFiles(fileIds,method);
    }

    /**
     * 批量文件移动或粘贴
     * @param newPath
     * @return
     */
    @SneakyThrows
    @GetMapping("/patse/files")
    public FreeResult pasteFiles(String newPath) {
        return fileUserService.pasteFiles(newPath);
    }

    /**
     * 单个文件夹复制（剪切、复制操作）
     * @return
     */
    @PostMapping("/copy/directory")
    public FreeResult copyDirectory(@RequestParam("directoryId") Long directoryId, String method){
        return fileUserService.copyDirectory(directoryId, method);
    }

    /**
     * 单个文件夹移动或粘贴
     * @param newPath
     * @return
     */
    @SneakyThrows
    @GetMapping("/patse/directory")
    public FreeResult pasteDirectory(String newPath) {
        return fileUserService.pasteDirectory(newPath);
    }
}

