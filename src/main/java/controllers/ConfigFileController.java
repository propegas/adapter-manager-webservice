/**
 * Copyright (C) 2012-2016 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import com.devdaily.system.AdapterManager;
import com.devdaily.system.ConfigFileContent;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.AdapterConfigFileDao;
import dao.AdapterDao;
import models.Adapter;
import models.AdapterConfigFile;
import models.AdapterConfigFileDto;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import java.util.List;
import java.util.Map;

@Singleton
public class ConfigFileController {

    private static final String ADAPTER = "adapter";
    private static final String CONFIG_FILE = "configFile";

    @Inject
    AdapterDao adapterDao;

    @Inject
    AdapterConfigFileDao adapterConfigFileDao;

    ///////////////////////////////////////////////////////////////////////////
    // Show adapter's all config files
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result configFiles(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        List<AdapterConfigFile> configs = adapterConfigFileDao.getAllConfigsByAdapterIdForPage(id);
        Map<String, Object> map = Maps.newHashMap();

        map.put("adapterConfigFiles", configs);

        return Results.html()
                .render("adapterConfigFiles", configs)
                .render(ADAPTER, adapter);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Show adapter's all config files
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result configFileNew(@PathParam("id") Long adapterId) {

        Adapter adapter;

        if (adapterId != null) {
            adapter = adapterDao.getAdapter(adapterId);
        } else {
            return Results.badRequest();
        }
        return Results.html().render(ADAPTER, adapter);
    }

    @FilterWith(SecureFilter.class)
    public Result configFileNewPost(@PathParam("id") Long adapterId,
                                    Context context,
                                    @JSR303Validation AdapterConfigFileDto configFileDto,
                                    Validation validation) {

        if (validation.hasViolations()) {

            context.getFlashScope().error("Please correct fields.");
            context.getFlashScope().put("configFile", configFileDto.configFile);

            return Results.redirect(String.format("/adapter/%d/configfile/new", adapterId));

        } else {

            adapterConfigFileDao.postConfigFile(adapterId, configFileDto);

            context.getFlashScope().success("New adapter's config file added.");

            return Results.redirect(String.format("/adapter/%d/configfiles", adapterId));

        }

    }

    @FilterWith(SecureFilter.class)
    public Result configFileRawView(@PathParam("id") Long adapterId,
                                    @PathParam("confid") Long confId,
                                    Context context) {

        Adapter adapter = null;
        if (adapterId != null) {
            adapter = adapterDao.getAdapter(adapterId);
        }

        AdapterConfigFile configFile = adapterConfigFileDao.getConfigFile(adapterId, confId);
        String confFile = configFile.getConfigFile();
        if (confFile == null || "".equals(confFile.trim()))
            return Results.notFound().render("Result", "Error");

        Map rawConfigFileResult = AdapterManager.showRawConfigFile(confFile);
        String content = "";
        String error = null;

        if ((boolean) rawConfigFileResult.get("result")) {
            context.getFlashScope().success("Content from Config file got successfully.\n");
            content = content + rawConfigFileResult.get("text");
        } else {
            context.getFlashScope().error("Error while getting Content from Config file");
            error = rawConfigFileResult.get("text").toString();
        }

        return Results.html()
                .render(ADAPTER, adapter)
                .render(CONFIG_FILE, configFile)
                .render("error", error)
                .render("content", content);

    }

    ///////////////////////////////////////////////////////////////////////////
    // Show adapter's all config files
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result configFileRawEdit(@PathParam("id") Long adapterId,
                                    @PathParam("confid") Long confId,
                                    Context context) {

        Adapter adapter;

        if (adapterId != null) {
            adapter = adapterDao.getAdapter(adapterId);
        } else {
            return Results.badRequest();
        }

        AdapterConfigFile configFile = adapterConfigFileDao.getConfigFile(adapterId, confId);
        String confFile = configFile.getConfigFile();
        if (confFile == null || "".equals(confFile.trim()))
            return Results.notFound().render("Result", "Error");

        Map rawConfigFileResult = AdapterManager.showRawConfigFile(confFile);
        String content = "";
        String error = null;

        if ((boolean) rawConfigFileResult.get("result")) {
            context.getFlashScope().success("Content from Config file got successfully.\n");
            content = content + rawConfigFileResult.get("text");
        } else {
            context.getFlashScope().error("Error while getting Content from Config file");
            error = rawConfigFileResult.get("text").toString();
        }

        return Results.html()
                .render(ADAPTER, adapter)
                .render(CONFIG_FILE, configFile)
                .render("error", error)
                .render("content", content);
    }

    @FilterWith(SecureFilter.class)
    public Result configFileRawEditPost(@PathParam("id") Long adapterId,
                                        @PathParam("confid") Long confId,
                                        Context context,
                                        ConfigFileContent configFileContent) {

        Adapter adapter = null;
        if (adapterId != null) {
            adapter = adapterDao.getAdapter(adapterId);
        }

        AdapterConfigFile configFile = adapterConfigFileDao.getConfigFile(adapterId, confId);

        String error = null;
        Map saveFileContentResult = AdapterManager.saveConfigFileContent(configFile, configFileContent);

        if ((boolean) saveFileContentResult.get("result"))
            context.getFlashScope().success("Config File Content saved.");
        else {
            context.getFlashScope().error("Error while Config File Content saving.");
            error = saveFileContentResult.get("text").toString();
        }

        return Results
                //.render("error", error)
                //.render(ADAPTER, adapter)
                //.render(CONFIG_FILE, configFile)
                //.render("content", configFileContent.getContent())
                .redirect(String.format("/adapter/%d/configfile/%d/fileview", adapterId, confId));
                //.template("views/ConfigFileController/configFiles.ftl.html");


    }

}
