package com.gyeom.infrastructure;

import com.gyeom.domain.AppConfiguration;
import com.gyeom.domain.ConfigRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalConfigRepository implements ConfigRepository {


    private static final String CONFIG_DIR_PATH = System.getProperty("user.dir") + "/config";
    private static final String CONFIG_PATH = CONFIG_DIR_PATH + "/backup-config.json";
    private static final File CONFIG_FILE = new File(CONFIG_PATH);

    @Override
    public boolean write(AppConfiguration config) {
        if (!CONFIG_FILE.exists()) {
            try {
                Files.createDirectories(Paths.get(CONFIG_DIR_PATH));
                //noinspection ResultOfMethodCallIgnored
                CONFIG_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (FileWriter fw = new FileWriter(CONFIG_FILE); BufferedWriter bw = new BufferedWriter(fw);) {
            bw.write(config.toJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean read(AppConfiguration config) {
        if (!CONFIG_FILE.exists()) {
            write(config);
        }

        try (FileReader fr = new FileReader(CONFIG_PATH); BufferedReader br = new BufferedReader(fr);) {
            StringBuilder stringBuilder = new StringBuilder();
            String str = null;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }

            config.fromJson(stringBuilder.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
