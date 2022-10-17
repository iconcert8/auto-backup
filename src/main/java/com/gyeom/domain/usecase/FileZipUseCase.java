package com.gyeom.domain.usecase;

import com.gyeom.domain.FileZipUtil;
import com.gyeom.model.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileZipUseCase {
    private final static Logger log = LoggerFactory.getLogger(FileZipUseCase.class);

    public FileVO call(List<FileVO> fileVOS) {
        if (fileVOS == null || fileVOS.isEmpty()) {
            log.warn("Source files is empty");
            return null;
        }
        log.info(String.format("Zipping '%s'", fileVOS));
        return FileZipUtil.zip(fileVOS);
    }
}
