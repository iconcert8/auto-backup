package com.gyeom.infrastructure;

import com.gyeom.controllers.UploadController;
import com.gyeom.domain.AppConfiguration;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class UploadControllerTest {

    private static UploadController controller;

    @BeforeAll
    static void beforeAll() {
        controller = new UploadController(new GoogleDriveFileRepository());
        AppConfiguration.getInstance().setBackUpMaxHistory(2);
    }

    @Test
    void upload() {
        controller.upload(new DirectoryPath(null, List.of("backup")),
                List.of(new FileVO("pom", DirectoryPath.of(new File("C:\\Users\\user\\Desktop"))),
                        new FileVO("agentlog.txt", DirectoryPath.of(new File("C:")))));
    }

    @Test
    void startUpload() throws InterruptedException {
        controller.startUpload(new DirectoryPath(null, List.of("Test")),
                List.of(new FileVO("agentlog.txt", DirectoryPath.of(new File("C:")))), 5000);
    }

}
