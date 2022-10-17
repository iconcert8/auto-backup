package com.gyeom.infrastructure;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.gyeom.domain.AppConstant;
import com.gyeom.domain.FileRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleDriveFileRepository implements FileRepository {

    private final static Logger log = LoggerFactory.getLogger(GoogleDriveFileRepository.class);
    private final static String CREDENTIAL_FILE_NAME = "google-drive-credential.json";
    private final static String TOKENS_DIRECTORY_PATH = "./tokens";

    private final static String ROOT_FOLDER = "root";


    public Credential getCredentials() throws IOException, GeneralSecurityException, RuntimeException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(CREDENTIAL_FILE_NAME);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), clientSecrets,
                List.of(DriveScopes.DRIVE)
        ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Drive getDrive() throws GeneralSecurityException, IOException {
        return new Drive
                .Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), getCredentials())
                .setApplicationName(AppConstant.APP_NAME)
                .build();
    }

    /**
     * @param dest
     * @return Google Drive Folder ID. <p>
     * Null return if folder is not found.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String searchFolder(DirectoryPath dest) throws IOException, GeneralSecurityException {
        return recursiveSearchFolder(dest, ROOT_FOLDER, 0);
    }

    private String recursiveSearchFolder(DirectoryPath dest, String parentFolderID, int current) throws GeneralSecurityException, IOException {
        if (dest.isRoot()) {
            return ROOT_FOLDER;
        }

        FileList result = getDrive().files().list()
                .setQ(String.format("name='%s' and mimeType='application/vnd.google-apps.folder' and '%s' in parents", dest.get(current), parentFolderID))
                .setSpaces("drive")
                .execute();

        if (result.getFiles().isEmpty())
            return null;

        if (current + 1 >= dest.getSize()) {
            return result.getFiles().get(0).getId();
        }

        return recursiveSearchFolder(dest, result.getFiles().get(0).getId(), current + 1);

    }

    public String searchFile(FileVO fileVO) throws GeneralSecurityException, IOException {
        String folderID = searchFolder(fileVO.getPath());
        FileList result = getDrive().files().list()
                .setQ(String.format("name='%s' and mimeType!='application/vnd.google-apps.folder' and '%s' in parents", fileVO.getName(), folderID))
                .setSpaces("drive")
                .execute();

        if (result.getFiles().isEmpty()) {
            return null;
        }

        return result.getFiles().get(0).getId();
    }


    /**
     * @param dest
     * @return Google Drive Folder ID.
     * @throws IOException
     */
    public String createFolder(DirectoryPath dest) throws IOException, GeneralSecurityException {
        return recursiveCreateFolder(dest, ROOT_FOLDER, 0);
    }

    private String recursiveCreateFolder(DirectoryPath dest, String parentFolderID, int current) throws GeneralSecurityException, IOException {
        if (dest.isRoot()) {
            throw new RuntimeException("Can't create root folder");
        }

        List<String> paths = new ArrayList<>();
        for (int i = 0; i <= current; i++) {
            paths.add(dest.get(i));
        }
        DirectoryPath tempDirPath = new DirectoryPath(dest.getPrefix(), paths);
        String folderID = searchFolder(tempDirPath);

        if (folderID == null) {
            File fileMetadata = new File();
            fileMetadata.setName(dest.get(current));
            if (!parentFolderID.equals(ROOT_FOLDER)) {
                fileMetadata.setParents(List.of(parentFolderID));
            }
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            File file = getDrive().files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            folderID = file.getId();
        }

        if (current + 1 >= dest.getSize()) {
            return folderID;
        }

        return recursiveCreateFolder(dest, folderID, current + 1);
    }

    /**
     * @param folderID If it is the root folder, add null or "".
     * @param fileVO
     * @return
     */
    public boolean uploadFile(String folderID, FileVO fileVO) {

        File fileMetadata = new File();
        fileMetadata.setName(fileVO.getName());
        if (folderID != null && !folderID.isEmpty()) {
            fileMetadata.setParents(List.of(folderID));
        }
//        fileMetadata.setName("Test");
//        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        java.io.File filePath = new java.io.File(fileVO.getFullName());
        FileContent mediaContent = new FileContent("", filePath);

        try {
            getDrive().files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (IOException | GeneralSecurityException e) {
            log.error(String.format("Failed to upload to Google Cloud. %s", fileVO.getFullName()));
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * @param folderID Google Drive folder ID. <p>
     *                 If it is the root folder, add null or "".
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<File> listFiles(String folderID) throws GeneralSecurityException, IOException {
        String tempFolderID = folderID;
        if (folderID == null || folderID.isEmpty()) {
            tempFolderID = ROOT_FOLDER;
        }

        FileList result =
                getDrive()
                        .files()
                        .list()
                        .setQ(String.format("mimeType!='application/vnd.google-apps.folder' and '%s' in parents", tempFolderID))
                        .setSpaces("drive")
//                        .setFields("files(id,name,createdTime,modifiedTime,size)")
                        .setFields("files(id,name,createdTime)")
                        .execute();

        return result.getFiles();
    }

    public boolean removeFile(String fileID) throws GeneralSecurityException, IOException {
        getDrive().files().delete(fileID).execute();
        return true;
    }

    @Override
    public boolean upload(DirectoryPath dest, FileVO fileVO) {
        if (dest == null || dest.isRoot()) {
            return uploadFile(null, fileVO);
        }

        String folderID = null;
        try {
            folderID = searchFolder(dest);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        if (folderID == null) {
            try {
                folderID = createFolder(dest);
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        return uploadFile(folderID, fileVO);
    }

    @Override
    public boolean remove(FileVO fileVO) {
        try {
            String fileID = searchFile(fileVO);
            if (fileID == null) {
                throw new RuntimeException(String.format("File in Google Drive can not found '%s'", fileVO));
            }
            return removeFile(fileID);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileVO> list(@NonNull DirectoryPath dest) {
        if (dest.isRoot()) {
            try {
                return listFiles(ROOT_FOLDER).stream()
                        .map(f -> new FileVO(f.getName(), DirectoryPath.of(dest), Instant.ofEpochMilli(f.getCreatedTime().getValue())))
                        .collect(Collectors.toList());
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        String folderID;
        try {
            folderID = searchFolder(dest);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        if (folderID == null) {
            throw new RuntimeException(String.format("Can not find folder '%s'", dest));
        }

        try {
            return listFiles(folderID).stream()
                    .map(f -> new FileVO(
                            f.getName(),
                            DirectoryPath.of(dest),
                            Instant.ofEpochMilli(f.getCreatedTime().getValue())
//                            Instant.now()
                    ))
                    .collect(Collectors.toList());
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
