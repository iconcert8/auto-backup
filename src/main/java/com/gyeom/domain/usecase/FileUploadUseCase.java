package com.gyeom.domain.usecase;

import com.gyeom.domain.FileRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadUseCase {
    private final static Logger log = LoggerFactory.getLogger(FileUploadUseCase.class);
    private final FileRepository fileRepository;

    public FileUploadUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Boolean call(DirectoryPath dest, FileVO fileVO) {
        if (fileVO == null) {
            log.warn("No files to upload");
            return false;
        }

        log.info(String.format("Uploading '%s'", fileVO));
        return fileRepository.upload(dest, fileVO);
    }
}
