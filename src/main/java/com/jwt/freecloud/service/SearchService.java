package com.jwt.freecloud.service;

import com.jwt.freecloud.common.dto.SearchDTO;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.util.FreeResult;

import java.io.IOException;
import java.util.List;

/**
 * @author：揭文滔
 * @date：2023/3/3 16:57
 */
public interface SearchService {

    FreeResult searchFiles(SearchDTO searchDTO);

    void updateEsByFileUser(FileUser fileUser);

    void updateEsByFileUsers(List<FileUser> fileUsers) throws IOException;

    void deleteEsByFileId(Long fileId);

    void deleteEsByFileIds(List<Long> ids);
}
