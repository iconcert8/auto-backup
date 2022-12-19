package com.gyeom;

import com.gyeom.controllers.UploadController;
import com.gyeom.domain.AppConfiguration;
import com.gyeom.infrastructure.GoogleDriveFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("----------- Back Up Program -------------");
        log.info("Hello Soo-bin!");
        log.info("Loading configuration...");
        AppConfiguration.getInstance().read();

        UploadController uploadController = new UploadController(new GoogleDriveFileRepository());
        log.info("Starting...");
        uploadController.startUpload(
                AppConfiguration.getInstance().getBackUpDirectoryPath(),
                AppConfiguration.getInstance().getSourceFiles(),
                AppConfiguration.getInstance().getBackUpPeriodMinutes() * 1000 * 60
//                10000
        );
    }
}