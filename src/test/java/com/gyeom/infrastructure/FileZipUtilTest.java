package com.gyeom.infrastructure;

import com.gyeom.domain.FileZipUtil;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class FileZipUtilTest {
    @Test
    void GetCurrentPath() {
        System.out.println(System.getProperty("user.dir"));
    }

    @Test
    void ZipTest() {
//        System.out.println(DateTimeFormatter.ofPattern("yyyyMMdd-HH-mm-ss").withZone(ZoneId.systemDefault()).format(Instant.now()));
        FileVO fileVO = FileZipUtil.zip(
                List.of(new FileVO("pom", DirectoryPath.of(new File("C:\\Users\\user\\Desktop"))),
                        new FileVO("agentlog.txt", DirectoryPath.of(new File("C:")))
                ));

        System.out.println(fileVO);
    }
}
