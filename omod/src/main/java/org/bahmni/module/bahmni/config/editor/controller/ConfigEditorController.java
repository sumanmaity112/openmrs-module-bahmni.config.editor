package org.bahmni.module.bahmni.config.editor.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bahmni.module.bahmni.config.editor.service.ConfigEditorService;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmni-config-editor")
public class ConfigEditorController extends BaseRestController implements ResourceLoaderAware {

    @Autowired
    private ConfigEditorService configEditorService;

    private ResourceLoader resourceLoader;
    private static String BASE_CONFIG_DIR = "/var/www/bahmni_config";


    @Autowired
    public ConfigEditorController(ConfigEditorService configEditorService) {
        this.configEditorService = configEditorService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String get() {
        return configEditorService.getFileStructure(BASE_CONFIG_DIR).toString();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/content")
    @ResponseBody
    public void getContent(HttpServletResponse response, @RequestParam(value = "filename", required = true) String filename) {
        String filePath = String.format("%s/%s", BASE_CONFIG_DIR, filename);
        try {
            Resource resource = resourceLoader.getResource("file:" + filePath);
            IOUtils.copy(new FileInputStream(resource.getFile()), response.getOutputStream());
            response.setCharacterEncoding("UTF-8");
            response.flushBuffer();
        } catch (IOException e) {
            throw new APIException("Unable to read [" + filePath + "]. Error [" + e.getMessage() + "]", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public void save(@RequestBody Map<String, String> body) {
        String filename = body.get("filename");
        String content = body.get("content");
        if (StringUtils.isEmpty(filename))
            throw new APIException("[filename] required");
        try {
            configEditorService.save(String.format("%s/%s", BASE_CONFIG_DIR, filename), content);
        } catch (IOException e) {
            throw new APIException("Unable to write content to [" + filename + "]. Error [" + e.getMessage() + "]", e);
        }
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
