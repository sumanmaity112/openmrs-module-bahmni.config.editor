package org.bahmni.module.bahmni.config.editor.service;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ConfigEditorService {
    private static String BASE_CONFIG_DIR_REGEXP = "^(/var/www/bahmni_config/|/var/www/bahmni_config)";


    public JSONObject getFileStructure(String path) throws IOException {
        int id = 1;
        File file = new File(path);
        return createStructure(file, createNode(id, file), id);
    }

    private JSONObject createNode(int id, File file) {
        JSONObject fileStructure = new JSONObject();

        fileStructure.put("name", file.getName());
        fileStructure.put("id", id);
        fileStructure.put("path", file.getAbsolutePath().replaceFirst(BASE_CONFIG_DIR_REGEXP, ""));
        return fileStructure;
    }

    private JSONObject createStructure(File path, JSONObject fileStructureHolder, int id) {
        if (path.isDirectory()) {
            fileStructureHolder.put("type", "folder");
            File[] files = path.listFiles();
            JSONArray childrens = new JSONArray();
            for (File file : files != null ? files : new File[0]) {
                childrens.put(createStructure(file, createNode(++id, file), id));
            }
            fileStructureHolder.put("children", childrens);
        } else {
            fileStructureHolder.put("type", "file");
            fileStructureHolder.put("extension", FilenameUtils.getExtension(path.getAbsolutePath()));
        }

        return fileStructureHolder;
    }

    public void save(String filename, String content) throws IOException {
        (new File(filename)).createNewFile();
        Path path = Paths.get(filename);
        Files.write(path, content.getBytes());
    }
}
