package com.gyeom.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.time.Instant;

public class FileVO {
    private static final String KEY_NAME = "name";
    private static final String KEY_PATH = "path";
    //    private static final String KEY_CREATED_DATE = "createdDate";
    private String name;
    private DirectoryPath path;
    private Instant createdDate;


    /**
     * File Information Object
     *
     * @param name file name
     *             ex) image.jpg
     * @param path file path without file name
     *             ex) C:/dev/path
     */
    public FileVO(String name, DirectoryPath path) {
        this.name = name;
        this.path = path;
    }

    public FileVO(String name, DirectoryPath path, Instant createdDate) {
        this(name, path);
        this.setCreatedDate(createdDate);
    }

    public static FileVO of(File file) {
        return new FileVO(file.getName(), DirectoryPath.of(file));
    }

    public static FileVO of(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String name = root.getAsJsonPrimitive(KEY_NAME).getAsString();
        DirectoryPath path = DirectoryPath.of(root.get(KEY_PATH).toString());

        return new FileVO(name, path);
    }

    /**
     * @return file name
     * ex) image.jpg
     */
    public String getName() {
        return name;
    }

    /**
     * @return file path without file name
     * ex) C:/dev/path
     */
    public DirectoryPath getPath() {
        return path;
    }

    /**
     * @return file name with path.
     * ex) C:/dev/path/image.jpg
     */
    public String getFullName() {
        return String.format("%s/%s", path, name);
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return String.format("{name: %s, path: %s}", name, path);
    }

    public String toJson() {
        JsonObject root = new JsonObject();
        root.add(KEY_NAME, new JsonPrimitive(this.name));
        root.add(KEY_PATH, JsonParser.parseString(this.path.toJson()));

        return root.toString();
    }

}
