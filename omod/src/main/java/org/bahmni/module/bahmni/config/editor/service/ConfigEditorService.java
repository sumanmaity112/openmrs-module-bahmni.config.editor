package org.bahmni.module.bahmni.config.editor.service;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ConfigEditorService {
    private static final String BASE_CONFIG_DIR_REGEXP = "^(/var/www/bahmni_config/|/var/www/bahmni_config)";
    private static final String DEFAULT_FILTER = ".*";

    public JSONObject getFileStructure(String path) {
        int id = 1;
        File file = new File(path);
        AdministrationService administrationService = Context.getAdministrationService();
        String fileFilter = administrationService.getGlobalProperty("bahmni.config.editor.file-filter", DEFAULT_FILTER);
        String folderFilter = administrationService.getGlobalProperty("bahmni.config.editor.folder-filter", DEFAULT_FILTER);
        return createStructure(file, createNode(id, file), id, fileFilter, folderFilter);
    }

    private JSONObject createNode(int id, File file) {
        JSONObject fileStructure = new JSONObject();

        fileStructure.put("name", file.getName());
        fileStructure.put("id", id);
        fileStructure.put("path", file.getAbsolutePath().replaceFirst(BASE_CONFIG_DIR_REGEXP, ""));
        return fileStructure;
    }

    private JSONObject createStructure(File path, JSONObject fileStructureHolder, int id, String fileFilter, String folderFilter) {
        if (path.isDirectory()) {
            fileStructureHolder.put("type", "folder");
            File[] files = path.listFiles();
            JSONArray childrens = new JSONArray();
            for (File file : files != null ? files : new File[0]) {
                if (isValid(file, fileFilter, folderFilter)) {
                    childrens.put(createStructure(file, createNode(++id, file), id, fileFilter, folderFilter));
                }
            }
            if (childrens.length() > 0) {
                fileStructureHolder.put("children", childrens);
            }
        } else {
            fileStructureHolder.put("type", "file");
            fileStructureHolder.put("extension", FilenameUtils.getExtension(path.getAbsolutePath()));
        }

        return fileStructureHolder;
    }

    private boolean isValid(File file, String fileFilter, String folderFilter) {
        boolean isDirectory = file.isDirectory();
        String name = file.getName();
        return (isDirectory && isMatchesFilter(folderFilter, name)) || (!isDirectory && isMatchesFilter(fileFilter, name));
    }

    private boolean isMatchesFilter(String filter, String name) {
        return name.matches(filter);
    }

    public void save(String filename, String content) throws IOException {
        (new File(filename)).createNewFile();
        Path path = Paths.get(filename);
        Files.write(path, content.getBytes());
    }
}
