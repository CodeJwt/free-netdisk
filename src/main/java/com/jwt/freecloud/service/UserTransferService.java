package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.dto.UploadDTO;
import com.jwt.freecloud.common.entity.UserTransfer;
import com.jwt.freecloud.util.FreeResult;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 用户传输表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface UserTransferService extends IService<UserTransfer> {

    FreeResult createFileUpload(UploadDTO uploadDTO);

    FreeResult completeMultipartUpload(String uploadId);

    FreeResult downloadFile(Integer fileId, HttpServletResponse response);
}
