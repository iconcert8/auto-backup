package com.gyeom.infrastructure;

import com.google.api.services.drive.model.File;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveFileRepositoryTest {
    static GoogleDriveFileRepository googleDrive;
    static FileVO fileVO;

    @BeforeAll
    static void beforeAll() {
        googleDrive = new GoogleDriveFileRepository();
        fileVO = new FileVO("pom.xml", DirectoryPath.of(new java.io.File("C:/Users/user/Desktop/pom")));
    }

    @Test
    void getCredential() {
        try {
            assertNotNull(googleDrive.getCredentials());
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("error");
            fail();
        }
    }

    @Test
    void searchFolder() throws IOException, GeneralSecurityException {
        assertNotNull(googleDrive.searchFolder(new DirectoryPath(null, List.of("Test"))));
//        System.out.println(googleDrive.searchFolder(new DirectoryPath(null, List.of("Test"))));
    }

    @Test
    void searchFile() throws GeneralSecurityException, IOException {
        assertNotNull(googleDrive.searchFile(new FileVO("pom.xml", new DirectoryPath(null, List.of("Test", "test")))));
    }

    @Test
    void create() throws GeneralSecurityException, IOException {
        assertNotNull(googleDrive.createFolder(new DirectoryPath(null, List.of("Test", "test"))));
//        System.out.println(googleDrive.createFolder(new DirectoryPath(null, List.of("Test", "test"))));
    }

    @Test
    void uploadFile() {
        googleDrive.uploadFile(null, fileVO);
    }

    @Test
    void upload() throws GeneralSecurityException, IOException {
        assertTrue(googleDrive.upload(new DirectoryPath(null, List.of("Test", "test")), fileVO));
    }

    @Test
    void listFiles() throws GeneralSecurityException, IOException {
        List<File> files = googleDrive.listFiles(null);
        files.forEach(f -> System.out.printf("name: %s, id: %s date: %s%n", f.getName(), f.getId(), f.getCreatedTime().toString()));
    }

    @Test
    void list() {
        List<FileVO> files = googleDrive.list(new DirectoryPath(null, List.of("Test", "test")));
        files.forEach(f -> System.out.printf("name: %s, path: %s date: %s%n", f.getName(), f.getFullName(), f.getCreatedDate().atZone(ZoneId.systemDefault())));
    }

    @Test
    void remove() {
        assertTrue(googleDrive.remove(new FileVO("pom.xml", new DirectoryPath(null, List.of("Test", "test")))));
    }

}