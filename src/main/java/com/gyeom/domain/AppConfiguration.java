package com.gyeom.domain;

import com.google.gson.*;
import com.gyeom.infrastructure.LocalConfigRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Application configuration data
 * <p>
 * The values initialized in the field are the default values.
 */
public class AppConfiguration {


    private static class LazyHolder {
        private static final AppConfiguration INSTANCE = new AppConfiguration();
    }

    public static AppConfiguration getInstance() {
        return LazyHolder.INSTANCE;
    }

    private AppConfiguration() {
        configRepository = new LocalConfigRepository();
    }

    private static final String KEY_BACKUP_MAX_HISTORY = "backUpMaxHistory";
    private static final String KEY_BACKUP_PERIOD_MINUTES = "backUpPeriodMinutes";
    private static final String KEY_TEMP_ZIP_FOLDER = "tempZipFolder";
    private static final String KEY_BACKUP_ZIP_FILE_NAME_PATTERN = "backUpZipFileNamePattern";
    private static final String KEY_BACKUP_DIRECTORY_PATH = "backUpDirectoryPath";
    private static final String KEY_SOURCE_FILES = "sourceFiles";

    private ConfigRepository configRepository;

    private int backUpMaxHistory = 10;
    private int backUpPeriodMinutes = 10;
    private String tempZipFolder = System.getProperty("user.dir") + "/backup";
    private String backUpZipFileNamePattern = "yyyyMMdd-HH-mm-ss";

    private DirectoryPath backUpDirectoryPath = new DirectoryPath(null, List.of("backup"));

    private List<FileVO> sourceFiles = List.of(new FileVO("path3", new DirectoryPath("C:", List.of("path1", "path2"))));

    public int getBackUpMaxHistory() {
        return backUpMaxHistory;
    }

    public DirectoryPath getBackUpDirectoryPath() {
        return backUpDirectoryPath;
    }

    public void setBackUpDirectoryPath(DirectoryPath backUpDirectoryPath) {
        this.backUpDirectoryPath = backUpDirectoryPath;
    }

    public List<FileVO> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<FileVO> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }


    public int getBakUpMaxHistory() {
        return backUpMaxHistory;
    }

    public int getBackUpPeriodMinutes() {
        return backUpPeriodMinutes;
    }

    public void setBackUpPeriodMinutes(int backUpPeriodMinutes) {
        this.backUpPeriodMinutes = backUpPeriodMinutes;
    }

    public String getTempZipFolder() {
        return tempZipFolder;
    }

    public String getBackUpZipFileNamePattern() {
        return backUpZipFileNamePattern;
    }

    public void setBackUpMaxHistory(int backUpMaxHistory) {
        this.backUpMaxHistory = backUpMaxHistory;
    }

    public void setTempZipFolder(String tempZipFolder) {
        this.tempZipFolder = tempZipFolder;
    }


    public void setBackUpZipFileNamePattern(String backUpZipFileNamePattern) {
        this.backUpZipFileNamePattern = backUpZipFileNamePattern;
    }

    public String toJson() {
        JsonObject root = new JsonObject();
        root.add(KEY_BACKUP_MAX_HISTORY, new JsonPrimitive(this.backUpMaxHistory));
        root.add(KEY_BACKUP_PERIOD_MINUTES, new JsonPrimitive(this.backUpPeriodMinutes));
        root.add(KEY_TEMP_ZIP_FOLDER, new JsonPrimitive(this.tempZipFolder));
        root.add(KEY_BACKUP_ZIP_FILE_NAME_PATTERN, new JsonPrimitive(this.backUpZipFileNamePattern));
        root.add(KEY_BACKUP_DIRECTORY_PATH, JsonParser.parseString(this.backUpDirectoryPath.toJson()));

        JsonArray sourceFiles = new JsonArray();
        if (!this.sourceFiles.isEmpty()) {
            this.sourceFiles.forEach(f -> sourceFiles.add(JsonParser.parseString(f.toJson())));
        }
        root.add(KEY_SOURCE_FILES, sourceFiles);

        return new GsonBuilder()
                .setPrettyPrinting()
                .create().toJson(root);
    }

    public void fromJson(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        setBackUpMaxHistory(root.getAsJsonPrimitive(KEY_BACKUP_MAX_HISTORY).getAsInt());
        setBackUpPeriodMinutes(root.getAsJsonPrimitive(KEY_BACKUP_PERIOD_MINUTES).getAsInt());
        setTempZipFolder(root.getAsJsonPrimitive(KEY_TEMP_ZIP_FOLDER).getAsString());
        setBackUpZipFileNamePattern(root.getAsJsonPrimitive(KEY_BACKUP_ZIP_FILE_NAME_PATTERN).getAsString());
        setBackUpDirectoryPath(DirectoryPath.of(root.get(KEY_BACKUP_DIRECTORY_PATH).toString()));

        List<FileVO> sourceFiles = new ArrayList<>();
        root.getAsJsonArray(KEY_SOURCE_FILES).forEach(sj -> sourceFiles.add(FileVO.of(sj.toString())));
        setSourceFiles(sourceFiles);
    }

    public boolean read() {
        return configRepository.read(this);
    }

    public boolean write() {
        return configRepository.write(this);
    }
}
