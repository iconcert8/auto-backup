package com.gyeom.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryPath {

    private static final String KEY_PREFIX = "prefix";
    private static final String KEY_PATHS = "paths";

    private String prefix;
    private List<String> paths;

    /**
     * @param prefix Path prefix <p>
     *               ex) C:, D:
     * @param paths  Full path <p>
     *               ex) "/path1/path2" => {"path1", "path2"}
     */
    public DirectoryPath(@Nullable String prefix, List<String> paths) {
        this.prefix = prefix == null || prefix.isEmpty() ? null : prefix;
        this.paths = paths.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public static DirectoryPath of(File file) {
        String dirPath = file.isDirectory() ? file.getPath() : file.getParent();
        dirPath = dirPath.replace('\\', '/');
        String prefixPath = "";
        String withoutPrefixPath = dirPath;
        if (dirPath.indexOf(':') != -1) {
            prefixPath = dirPath.substring(0, dirPath.indexOf(':') + 1);
            withoutPrefixPath = withoutPrefixPath.substring(withoutPrefixPath.indexOf(':') + 1);
        }

        return new DirectoryPath(prefixPath, List.of(withoutPrefixPath.split("/")));
    }

    public static DirectoryPath of(DirectoryPath directoryPath) {
        List<String> paths = new ArrayList<>();
        String prefix = directoryPath.getPrefix();
        if (!directoryPath.isRoot()) {
            for (int i = 0; i < directoryPath.getSize(); i++) {
                String path = directoryPath.get(i);
                paths.add(path);
            }
        }

        return new DirectoryPath(prefix, paths);
    }

    public static DirectoryPath of(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String prefix = root.get(KEY_PREFIX) == null
                ? null
                : root.getAsJsonPrimitive(KEY_PREFIX).getAsString();
        List<String> paths = new Gson().fromJson(
                root.getAsJsonArray(KEY_PATHS),
                new TypeToken<ArrayList<String>>() {
                }.getType());

        return new DirectoryPath(prefix, paths);
    }

    public int getSize() {
        return paths.size();
    }

    public String getPrefix() {
        return prefix;
    }

    public String get(int index) {
        return paths.get(index);
    }

    public boolean isRoot() {
        if (paths.size() == 0)
            return true;

        return false;
    }

    @Override
    public String toString() {
        String tempPrefix = prefix == null ? "" : prefix;
        return tempPrefix + paths.stream()
                .map(p -> String.format("/%s", p))
                .reduce("", (p1, p2) -> p1 + p2);
    }

    public String toJson() {
        JsonObject root = new JsonObject();
        root.add(KEY_PREFIX, prefix == null ? null : new JsonPrimitive(prefix));

        JsonArray paths = new JsonArray();
        if (!this.paths.isEmpty()) {
            this.paths.forEach(paths::add);
        }

        root.add(KEY_PATHS, paths);

        return root.toString();
    }

}
