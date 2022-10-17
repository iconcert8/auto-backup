package com.gyeom.domain.usecase;

import com.gyeom.domain.AppConfiguration;
import com.gyeom.domain.FileRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class BackupCountManageUseCase {
    private final static Logger log = LoggerFactory.getLogger(BackupCountManageUseCase.class);

    private final FileRepository fileRepository;

    public BackupCountManageUseCase(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void call(DirectoryPath dest) {
        if (dest == null) {
            log.warn("The destination folder is not set.");
            return;
        }

        List<FileVO> fileVOS = fileRepository.list(dest);
        if (fileVOS.size() > AppConfiguration.getInstance().getBakUpMaxHistory()) {
            FileVO oldestFile = fileVOS.stream().min(Comparator.comparing(FileVO::getCreatedDate)).orElse(null);
            if (oldestFile == null) {
                return;
            }

//            log.info(String.format("The number of files in the folder '%s' is higher than the limit of '%s'.", oldestFile.getPath(), AppConfiguration.getInstance().getBakUpMaxHistory()));
            log.info(String.format("Folder limit count '%s', Deleting oldest file '%s'", AppConfiguration.getInstance().getBakUpMaxHistory(), oldestFile));
            fileRepository.remove(oldestFile);
        }
    }

}
