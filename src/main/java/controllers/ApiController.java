/**
 * Copyright (C) 2012 the original author or authors.
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
import com.google.inject.Inject;
import dao.AdapterConfFileKeyDao;
import dao.AdapterConfigFileDao;
import dao.AdapterDao;
import dao.AdapterTemplateDao;
import dao.AdapterTemplatePropertyDao;
import dao.GlobalPropertyDao;
import dao.UserAuthDao;
import etc.LoggedInUser;
import models.Adapter;
import models.AdapterConfigFile;
import models.AdapterConfigFileDto;
import models.AdapterConfigFileProperty;
import models.AdapterConfigFilesDto;
import models.AdapterDto;
import models.AdapterTemplate;
import models.AdapterTemplateProperty;
import models.AdapterTemplatesDto;
import models.AdaptersDto;
import models.GlobalProperty;
import models.TemplateConfigFilePropertiesDto;
import models.TemplatePropertyDto;
import models.UserAuth;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import templates.ConfProperty;
import templates.ConfigFile;
import templates.MainConfigProperty;
import templates.Property;
import templates.Template;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private static final String RESULT_FIELD_NAME = "Result";

    private List<Property> properties;

    @Inject
    AdapterDao adapterDao;

    @Inject
    UserAuthDao userAuthDao;

    @Inject
    AdapterConfigFileDao configFileDao;

    @Inject
    AdapterTemplateDao adapterTemplateDao;

    @Inject
    AdapterTemplatePropertyDao adapterTemplatePropertyDao;

    @Inject
    GlobalPropertyDao globalPropertyDao;

    @Inject
    AdapterConfFileKeyDao configFilePropertyDao;

    //@FilterWith(SecureFilter.class)
    public Result getAdaptersJson() {

        AdaptersDto adaptersDto = adapterDao.getAllAdapters();

        return Results.json().render(adaptersDto);

    }

    //@FilterWith(SecureFilter.class)
    public Result getAdapterLogJson(@PathParam("id") Long id,
                                    @Param("size") int size,
                                    @Param("type") String type
    ) {

        Adapter adapter = null;

        if (id != null) {
            adapter = adapterDao.getAdapter(id);
        }
        if (id == null || adapter == null) {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", "ID not found");
        }

        String log = "";
        String error;

        Map logResult = AdapterManager.showLogAdapter(id, adapter, size, type);

        if ((boolean) logResult.get("result")) {
            log = log + logResult.get("text");
            return Results.json()
                    .render(RESULT_FIELD_NAME, "Success")
                    .render("Message", log);
        } else {
            error = logResult.get("text").toString();
            return Results.notFound().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", error);
        }

    }

    public Result getAdaptersXml() {

        AdaptersDto adaptersDto = adapterDao.getAllAdapters();

        return Results.xml().render(adaptersDto);

    }

    //@FilterWith(SecureFilter.class)
    public Result getAdapterJson(@PathParam("id") Long id) {

        Adapter adapter = adapterDao.getAdapter(id);

        return Results.json().render(adapter);

    }

    //@FilterWith(SecureFilter.class)
    public Result postAdapterJson(@LoggedInUser String username,
                                  AdapterDto adapterDto) {

        Adapter createdAdapter = adapterDao.postAdapter(adapterDto);
        logger.debug(String.format("****Receive created adapter: %s,%nid: %d", createdAdapter, createdAdapter.id));
        if (createdAdapter.id == null) {
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");
        } else {
            return Results.json().render(RESULT_FIELD_NAME, "Success");
        }

    }

    //@FilterWith(SecureFilter.class)
    public Result postAdapterXml(@LoggedInUser String username,
                                 AdapterDto adapterDto) {

        Adapter createdAdapter = adapterDao.postAdapter(adapterDto);

        if (createdAdapter.id == null) {
            return Results.notFound();
        } else {
            return Results.xml();
        }

    }

    /*
    @Param("username") String username,
    @Param("password") String password,
    @Param("rememberMe") Boolean rememberMe,
     */
    public Result apiLoginPost(UserAuth userAuth,
                               Context context) {

        logger.debug("**** userAuth: " + userAuth);
        if (userAuth != null) {
            logger.info("**** userAuth.username: " + userAuth.username);

            boolean isUserNameAndPasswordValid = userAuthDao.isUserAndPasswordValid(userAuth.username, userAuth.password);

            if (isUserNameAndPasswordValid) {
                Session session = context.getSession();
                session.put("username", userAuth.username);
                session.setExpiryTime(1 * 5 * 60 * 1000L);

                logger.debug("**** Token:%s %s [%n]",
                        session.getAuthenticityToken(),
                        session.getId());

                context.getFlashScope().success("login.loginSuccessful");

                return Results.ok().json().render("Result", "Successful").render("token", session.getAuthenticityToken());

            } else {

                // something is wrong with the input or password not found.
                context.getFlashScope().put("username", userAuth.username);
                context.getFlashScope().error("login.errorLogin");

                return Results.forbidden().json().render(RESULT_FIELD_NAME, "Forbidden");

            }
        }

        return Results.badRequest().json().render(RESULT_FIELD_NAME, "Bad request");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Logout
    ///////////////////////////////////////////////////////////////////////////
    //@FilterWith(SecureFilter.class)
    public Result apiLogout(Context context) {

        // remove any user dependent information
        Session session = context.getSession();
        logger.debug("****1 Token:%s %s%n",
                session.getAuthenticityToken(),
                session.getId());
        session.clear();

        logger.debug("****2 Token:%s %s [%n]",
                session.getAuthenticityToken(),
                session.getId());

        context.getFlashScope().success("login.logoutSuccessful");

        return Results.ok().json().render(RESULT_FIELD_NAME, "Logout");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Auth check
    ///////////////////////////////////////////////////////////////////////////
    //@FilterWith(SecureFilter.class)
    public Result apiAuthCheck(Context context) {

        // if we got no cookies we break:
        if (context.getSession() == null
                || context.getSession().get("username") == null) {

            return Results.forbidden().json().render(RESULT_FIELD_NAME, "Not active");

        } else {
            return Results.ok().json().render(RESULT_FIELD_NAME, "Active");
        }

    }


    //////////////////////////////////////////////////////
    // Config files part
    //////////////////////////////////////////////////////

    public Result getConfigFilesJson(@PathParam("id") Long adapterId) {

        AdapterConfigFilesDto adapterConfigFilesDto = configFileDao.getAllConfigsByAdapterId(adapterId);
        logger.info(adapterConfigFilesDto.toString());
        return Results.json().render(adapterConfigFilesDto);

    }

    //@FilterWith(SecureFilter.class)
    public Result getConfigFileJson(@PathParam("id") Long adapterId,
                                    @PathParam("confid") Long confId) {

        AdapterConfigFile configFile = configFileDao.getConfigFile(adapterId, confId);

        return Results.json().render(configFile);

    }

    //@FilterWith(SecureFilter.class)
    public Result postConfigFileJson(@PathParam("id") Long adapterId,
                                     AdapterConfigFileDto configFileDto) {

        logger.debug("[TEST] configFileDto: " + configFileDto);
        logger.debug("[TEST] adapterId: " + adapterId);

        AdapterConfigFile createdConfigFile = configFileDao.postConfigFile(adapterId, configFileDto);
        logger.debug(String.format("****Receive created config file: %s,%nid: %d",
                createdConfigFile, createdConfigFile.getId()));

        if (createdConfigFile.getId() == null) {
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");
        } else {
            return Results.json().render(RESULT_FIELD_NAME, "Success");
        }

    }

    ////////////////////////////////////////////////////////////////////////
    // Get Config File raw content
    ////////////////////////////////////////////////////////////////////////
    //@FilterWith(SecureFilter.class)
    public Result getConfigFileRawContentJson(@PathParam("id") Long adapterId,
                                              @PathParam("confid") Long confId) {

        AdapterConfigFile configFile = configFileDao.getConfigFile(adapterId, confId);
        String confFile = configFile.getConfigFile();
        if (confFile == null || "".equals(confFile.trim()))
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");

        Map rawConfigFileResult = AdapterManager.showRawConfigFile(confFile);
        if ((boolean) rawConfigFileResult.get("result")) {
            return Results.json()
                    .render("Message", rawConfigFileResult.get("text"))
                    .render(RESULT_FIELD_NAME, "Success");
        } else {
            return Results.notFound().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", rawConfigFileResult.get("text"));
        }

    }

    //@FilterWith(SecureFilter.class)
    public Result postConfigFileRawContentJson(@PathParam("id") Long adapterId,
                                               @PathParam("confid") Long confId,
                                               ConfigFileContent configFileContent) {

        logger.debug("[TEST] configFileContent: " + configFileContent);
        logger.debug("[TEST] adapterId: " + adapterId);

        AdapterConfigFile configFile = configFileDao.getConfigFile(adapterId, confId);
        String confFile = configFile.getConfigFile();
        if (confFile == null || "".equals(confFile.trim()))
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");

        Map rawConfigFileResult = AdapterManager.saveConfigFileContent(configFile, configFileContent);
        if ((boolean) rawConfigFileResult.get("result")) {
            return Results.json()
                    .render("Message", rawConfigFileResult.get("text"))
                    .render(RESULT_FIELD_NAME, "Success");
        } else {
            return Results.notFound().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", rawConfigFileResult.get("text"));
        }
    }

    ///

    //@FilterWith(SecureFilter.class)
    public Result getAdapterTemplatesJson() {

        AdapterTemplatesDto allAdapterTemplates = adapterTemplateDao.getAllAdapterTemplates();

        return Results.json().render(allAdapterTemplates);

    }

    //@FilterWith(SecureFilter.class)
    public Result getAdapterTemplateJson(@PathParam("id") Long templateId) {

        AdapterTemplate adapterTemplateById = adapterTemplateDao.getAdapterTemplateById(templateId);

        return Results.json().render(adapterTemplateById);

    }

    //@FilterWith(SecureFilter.class)
    public Result getAdapterTemplatePropertiesJson(@PathParam("id") Long templateId) {

        AdapterTemplate adapterTemplateById = adapterTemplateDao.getAdapterTemplateById(templateId);

        Template template = initTemplate(adapterTemplateById.getTemplateXmlPath());
        List<Property> propertyList = template.getProperties().getProperty();
        logger.info(propertyList.toString());

        return Results.json().render(propertyList);

    }

    private Template initTemplate(String filePath) {
        Template template = null;

        logger.info(String.format("Get access to xml: %s ", filePath));
        try {

            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(Template.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            template = (Template) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            logger.error("Error while invoke Parse config from XML: ", e);
        }
        return template;
    }

    public Result postAdapterTemplatePropertiesJson(@PathParam("id") Long templateId,
                                                    @JSR303Validation TemplatePropertyDto templatePropertyDto,
                                                    Validation validation) {

        if (validation.hasViolations()) {

            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", validation.getBeanViolations());

        }

        AdapterTemplate adapterTemplateById = adapterTemplateDao.getAdapterTemplateById(templateId);
        Template template = initTemplate(adapterTemplateById.getTemplateXmlPath());

        properties = template.getProperties().getProperty();

        // translate Properties as HashMap
        HashMap<String, String> propKeys = null;
        try {
            propKeys = initPropertiesMapFromList(templatePropertyDto);
        } catch (IllegalAccessException e) {
            logger.error("Error while getting access to Property class fields", e);
        }

        if (propKeys == null) {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", "Ошибка при чтении и формировании параметров шаблона адаптера из XML");
        }
        logger.debug(propKeys.toString());

        // check directories
        Result checkDirsResult = checkDirsResult(propKeys);
        if (checkDirsResult != null)
            return checkDirsResult;

        // check for unique
        Result checkResult = checkPropertiesOnUniq();
        if (checkResult != null)
            return checkResult;

        // create XML string from object and save as a file
        return saveObjectsToXml(template, propKeys);

    }

    private Result saveObjectsToXml(Template template, HashMap<String, String> propKeys) {
        String xmlString;
        try {
            String xmlFileId = Long.toString(System.currentTimeMillis());
            Template templateWithProperties;
            Template templateSave;
            // replace main Properties Placeholders
            xmlString = xmlTemplateToString(template, propKeys);
            logger.debug("XML: " + xmlString);
            // save as new xml file
            templateWithProperties = saveTemplateToFile(xmlFileId, xmlString);

            // replace Placeholders for GlobalConfigs
            xmlString = xmlTemplateToStringWithGlobalProperties(templateWithProperties);
            logger.debug("XML: " + xmlString);

            // save as new xml file
            templateSave = saveTemplateToFile(xmlFileId, xmlString);

            logger.debug("******: " + templateSave.getConfigFiles().getConfigFile().get(0).getConfProperties().get("delay").getValue());

            return Results.ok().json()
                    .render(RESULT_FIELD_NAME, "Success")
                    .render("configFiles", templateSave.getConfigFiles().getConfigFile())
                    .render("file", String.format("templates/%s.xml", xmlFileId))
                    .render("xmlFileId", String.format("%s", xmlFileId));


        } catch (JAXBException e) {
            String error = "Error while format string from XML and saving template to XML file: ";
            logger.error(error, e);
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("message", error + e);
        }
    }

    private Result checkPropertiesOnUniq() {
        for (Property property : properties) {
            logger.info("property: " + property);
            if (property.isUnique()) {
                logger.info(String.format("*** property name: %s", property.getName()));
                logger.info(String.format("*** property value: %s", property.getValue()));
                List<AdapterTemplateProperty> a = adapterTemplatePropertyDao
                        .getPropertiesByNameAndValue(property.getName(), property.getValue());
                if (!a.isEmpty()) {
                    return Results.badRequest().json()
                            .render(RESULT_FIELD_NAME, "Error")
                            .render("Message", String.format("Поле '%s' должно быть уникальным в системе", property.getLabel()));
                }
            }
        }
        return null;
    }

    private Result checkDirsResult(HashMap<String, String> propKeys) {
        File adaptersDirectoryFullPath = new File(propKeys.get("adaptersDirectoryFullPath"));
        File adapterDirectory = new File(String.format("%s/%s",
                propKeys.get("adaptersDirectoryFullPath"),
                propKeys.get("adapterDirectory")));
        if (!adaptersDirectoryFullPath.exists()) {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", String.format("Директория '%s' не найдена",
                            adaptersDirectoryFullPath.toString()));
        }
        if (adapterDirectory.exists()) {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", String.format("Директория '%s' уже существует",
                            adapterDirectory.toString()));
        }
        return null;
    }

    private String xmlTemplateToStringWithGlobalProperties(Template template) {
        String xmlString;

        List<GlobalProperty> globalProperties = globalPropertyDao.getAllProperties();

        try {
            JAXBContext context = JAXBContext.newInstance(Template.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(template, sw);
            xmlString = sw.toString();

            // replace all ${} placeholders in template with property
            for (GlobalProperty globalProperty : globalProperties) {
                xmlString = xmlString.replaceAll(String.format("\\$\\{%s\\}",
                        globalProperty.getPropertyName()),
                        globalProperty.getPropertyValue());
            }

            return xmlString;


        } catch (JAXBException e) {
            logger.error("Error while format string from XML: ", e);
            return null;
        }
    }

    public Result postTemplateConfFilePropertiesJson(@PathParam("id") Long templateId,
                                                     @PathParam("xmlfileid") String fileId,
                                                     @PathParam("confid") String confIId,
                                                     TemplateConfigFilePropertiesDto configFilePropertiesDto) {
        String fileXml = String.format("templates/%s.xml", fileId);
        Template template = initTemplate(String.format(fileXml, fileId));
        List<ConfigFile> configFiles = template.getConfigFiles().getConfigFile();

        // get conffile by id from xml
        ConfigFile configFile = getConfigFileFromListById(configFiles, confIId);
        if (configFile == null) {
            String error = "Описание конфигурационного файла не найдено в шаблоне XML";
            logger.error(error, new NullPointerException());
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("message", error);
        }

        logger.debug("*****RESULT: " + configFilePropertiesDto.getConfFileProperties());
        // check every key from conf properties
        for (Map.Entry<String, Object> entry : configFilePropertiesDto.getConfFileProperties().entrySet()) {
            logger.debug(String.format("*** %s: %s",
                    entry.getKey(),
                    entry.getValue().toString()));

            // save values to XML
            // check Global flag's field for empty value
            ConfProperty confProperty = configFile.getConfProperties().get(entry.getKey());
            if ("".equals(entry.getValue().toString()) &&
                    !confProperty.isGlobalConfig()) {
                return Results.badRequest().json()
                        .render(RESULT_FIELD_NAME, "Error")
                        .render("message", String.format("Поле '%s' не может быть пустым для данной конфигурации",
                                confProperty.getLabel()));
            }

            String fieldPattern = confProperty.getFormat();

            // check format
            logger.info(String.format("Try to check field %s with value: %s on format:%s",
                    confProperty.getName(),
                    entry.getValue().toString(),
                    fieldPattern));
            if (!entry.getValue().toString().matches(fieldPattern))
                return Results.badRequest().json()
                        .render(RESULT_FIELD_NAME, "Error")
                        .render("message", String.format("Поле '%s' не соответствует формату: %s ",
                                confProperty.getLabel(),
                                confProperty.getFormat()));

            // save value to XML
            confProperty.setValue(entry.getValue().toString());

            // save values to DB

        }

        String xmlString = xmlTemplateToString(template);
        // save xml to file
        try {
            saveTemplateToFile(fileId, xmlString);
        } catch (JAXBException e) {
            String error = "Ошибка при формировании и сохранении параметров в файл шаблона ";
            logger.error(error, e);
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("message", error);
        }

        return Results.ok().json()
                .render(RESULT_FIELD_NAME, "Success");
        //.render("configFile", configFile);
    }

    public Result postCreateAdapterFromXmlTemplateJson(@PathParam("id") Long templateId,
                                                       @PathParam("xmlfileid") String fileId) {


        String fileXml = String.format("templates/%s.xml", fileId);
        Template template = initTemplate(String.format(fileXml, fileId));

        String resultMessage = "";

        // move dir and files
        Map initAdapterFilesResult = AdapterManager.initAdapterFiles(template);
        if ((boolean) initAdapterFilesResult.get("result")) {
            resultMessage += "";
        } else {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", initAdapterFilesResult.get("text"));
        }

        File destAdapterDir = (File) initAdapterFilesResult.get("dir");

        resultMessage += "Директория с адаптером создана. ";

        // create adapter in DB
        Adapter createdAdapter = createAdapterFromXml(template);

        if (createdAdapter == null) {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", "Ошибка при чтении XML и создания адаптера");
        }
        resultMessage += "Адаптер создан. ";

        List<ConfigFile> configFileList = template.getConfigFiles().getConfigFile();
        List<AdapterConfigFile> createdAdapterConfigFiles = new ArrayList<>();
        for (ConfigFile configFile : configFileList) {
            AdapterConfigFileDto configFileDto = new AdapterConfigFileDto();
            configFileDto.setConfigDescription(configFile.getDescription());
            configFileDto.configFile = configFile.getConfigFile();
            Map<String, ConfProperty> templateConfFileKeys = configFile.getConfProperties();
            Map<String, AdapterConfigFileProperty> configFileKeys = new HashMap<>();
            for (Map.Entry<String, ConfProperty> entry : templateConfFileKeys.entrySet()) {
                AdapterConfigFileProperty confProperty = new AdapterConfigFileProperty();
                confProperty.setPropertyName(entry.getValue().getName());
                confProperty.setPropertyLabel(entry.getValue().getLabel());
                confProperty.setPropertyValue(entry.getValue().getValue());
                configFileKeys.put(entry.getKey(), confProperty);
            }

            configFileDto.setConfFileProperties(configFileKeys);
            //logger.info(configFile.getConfProperties());
            AdapterConfigFile createdConfFile = configFileDao.postConfigFile(createdAdapter.id, configFileDto);
            if (createdConfFile == null) {
                // delete adapter
                adapterDao.deleteAdapter(createdAdapter.id, createdAdapter);
                try {
                    FileUtils.deleteDirectory(destAdapterDir);
                } catch (Exception e1) {
                    logger.error("Ошибка при удалении директории после сбоя установки ", e1);
                }
                return Results.badRequest().json()
                        .render(RESULT_FIELD_NAME, "Error")
                        .render("Message", "Ошибка при создании конфигурационного файла адаптера: " + configFile.getConfigFile());
            }
            createdAdapterConfigFiles.add(createdConfFile);

            // replace variables in config files
            boolean succeededReplace = replaceVariablesInConfig(template, configFile);
            if (!succeededReplace) {
                // delete adapter
                adapterDao.deleteAdapter(createdAdapter.id, createdAdapter);
                try {
                    FileUtils.deleteDirectory(destAdapterDir);
                } catch (Exception e1) {
                    logger.error("Ошибка при удалении директории после сбоя установки ", e1);
                }
                return Results.badRequest().json()
                        .render(RESULT_FIELD_NAME, "Error")
                        .render("Message", "Ошибка при замене переменных в конфигурационном файла адаптера: " + configFile.getConfigFile());
            }

        }
        resultMessage += "Конфигурационные файлы созданы. ";

        // save Properties in DB
        adapterTemplatePropertyDao.postProperties(fileId, properties);

        resultMessage += "Параметры шаблона сохранены. ";

        return Results.ok().json()
                .render("Adapter", createdAdapter)
                .render("ConfigFiles", createdAdapterConfigFiles)
                .render("Operations", resultMessage)
                .render(RESULT_FIELD_NAME, "Success");
    }

    //@FilterWith(SecureFilter.class)
    public Result adapterManagePost(@PathParam("id") Long id,
                                    @Param("command") String command) {

        Adapter adapter = null;
        if (id != null)
            adapter = adapterDao.getAdapter(id);

        String error = null;
        Map commandResult = new HashMap<>();

        if ("stop".equals(command)) {
            logger.info("Пытаемся выполнить команду остановки адаптера...");
            commandResult = AdapterManager.stopAdapter(id, adapter);
        } else if ("start".equals(command)) {
            logger.info("Пытаемся выполнить команду остановки адаптера...");
            commandResult = AdapterManager.startAdapter(id, adapter);
        } else {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", "Неверный тип команды");

        }

        if ((boolean) commandResult.get("result")) {
            return Results.ok().json()
                    .render(RESULT_FIELD_NAME, "Success")
                    .render("Message", commandResult.get("text"));
        } else {
            return Results.badRequest().json()
                    .render(RESULT_FIELD_NAME, "Error")
                    .render("Message", commandResult.get("text"));
        }

    }

    private boolean replaceVariablesInConfig(Template template, ConfigFile configFile) {

        logger.info(String.format("Замена переменных в файле %s...",
                configFile.getConfigFile()));

        Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get(configFile.getConfigFile());
        switch (configFile.getTargetEncoding()) {
            case "UTF-8":
                charset = StandardCharsets.UTF_8;
                break;
            case "ISO-8859-1":
                charset = StandardCharsets.ISO_8859_1;
                break;
        }

        properties = template.getProperties().getProperty();
        logger.info(String.format("Замена переменных в файле %s параметрами шаблона",
                configFile.getConfigFile()));
        for (Property property : properties) {

            try {
                String content = new String(Files.readAllBytes(path), charset);
                content = content.replaceAll(String.format("\\$\\{%s\\}",
                        property.getName()),
                        property.getValue().trim());
                Files.write(path, content.getBytes(charset));
            } catch (IOException e) {
                String error = "Ошибка при формировании и сохранении параметров в файл конфигурации ";
                logger.error(error, e);
                return false;
            }
        }

        logger.info(String.format("Замена переменных в файле %s конфигурационными параметрами",
                configFile.getConfigFile()));
        Map<String, ConfProperty> confProperties = configFile.getConfProperties();
        for (Map.Entry<String, ConfProperty> entry : confProperties.entrySet()) {
            try {
                String content = new String(Files.readAllBytes(path), charset);
                content = content.replaceAll(String.format("\\$\\{%s\\}",
                        entry.getKey()),
                        entry.getValue().getValue().trim());
                Files.write(path, content.getBytes(charset));
            } catch (IOException e) {
                String error = "Ошибка при формировании и сохранении конфигурации в файл ";
                logger.error(error, e);
                return false;
            }

            //configFilePropertyDao.
        }


        return true;
    }

    private Adapter createAdapterFromXml(Template template) {
        List<MainConfigProperty> propertyList = template.getMainConfigProperties().getMainConfigProperty();
        HashMap<String, MainConfigProperty> propKeys = null;
        try {
            propKeys = convertPropertiesMapFromList(propertyList);
        } catch (IllegalAccessException e) {
            logger.error("Ошибка доступа к полям объекта: ", e);
        }
        if (propKeys == null) {
            return null;
        }
        AdapterDto adapterDto = new AdapterDto();
        logger.debug("******propKeys: " + propKeys);
        adapterDto.title = propKeys.get("title").getValue().trim();
        adapterDto.content = propKeys.get("content").getValue().trim();
        adapterDto.jarFileName = propKeys.get("jarFileName").getValue().trim();
        adapterDto.jarFilePath = propKeys.get("jarFilePath").getValue().trim();
        adapterDto.checkStatusCommands = propKeys.get("checkStatusCommands").getValue().trim();
        adapterDto.startCommands = propKeys.get("startCommands").getValue().trim();
        adapterDto.stopCommands = propKeys.get("stopCommands").getValue().trim();
        adapterDto.logFile = propKeys.get("logFile").getValue().trim();
        adapterDto.errorLogFile = propKeys.get("errorLogFile").getValue().trim();
        // adapterDto.status = propKeys.get("content").getValue();

        return adapterDao.postAdapter(adapterDto);
    }

    private ConfigFile getConfigFileFromListById(List<ConfigFile> configFiles, String confId) {
        for (ConfigFile configFile : configFiles) {
            if (confId.equals(configFile.getId()))
                return configFile;
        }
        return null;
    }

    private Template saveTemplateToFile(String xmlFileId, String xmlString) throws JAXBException {
        File newXmlFile = new File(String.format("templates/%s.xml", xmlFileId));

        JAXBContext context = JAXBContext.newInstance(Template.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringReader reader = new StringReader(xmlString);
        Template templateSave = (Template) unmarshaller.unmarshal(reader);
        m.marshal(templateSave, newXmlFile);
        return templateSave;

    }

    private String xmlTemplateToString(Template template) {
        // create XML string from object
        String xmlString;
        try {
            JAXBContext context = JAXBContext.newInstance(Template.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(template, sw);
            xmlString = sw.toString();

            return xmlString;

        } catch (JAXBException e) {
            logger.error("Error while format string from XML: ", e);
            return null;
        }
    }

    private String xmlTemplateToString(Template template, HashMap<String, String> propKeys) {
        // create XML string from object
        String xmlString;
        try {
            JAXBContext context = JAXBContext.newInstance(Template.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(template, sw);
            xmlString = sw.toString();

            // replace all ${} placeholders in template with property
            for (Map.Entry<String, String> entry : propKeys.entrySet()) {
                xmlString = xmlString.replaceAll(String.format("\\$\\{%s\\}", entry.getKey()),
                        entry.getValue());
            }

            return xmlString;


        } catch (JAXBException e) {
            logger.error("Error while format string from XML: ", e);
            return null;
        }
    }

    private HashMap<String, String> initPropertiesMapFromList(TemplatePropertyDto templatePropertyDto)
            throws IllegalAccessException {

        Field[] fields = TemplatePropertyDto.class.getDeclaredFields();
        HashMap<String, String> propKeys = new HashMap<>();
        for (Field field : fields) {
            //gives the names of the fields
            for (Property property : properties) {
                if (property.getName().equals(field.getName())) {
                    property.setValue(field.get(templatePropertyDto).toString().trim());
                    propKeys.put(property.getName(), property.getValue().trim());
                }
            }
        }
        return propKeys;
    }

    private HashMap<String, MainConfigProperty> convertPropertiesMapFromList(List<MainConfigProperty> propertyList)
            throws IllegalAccessException {

        //Field[] fields = TemplatePropertyDto.class.getDeclaredFields();
        HashMap<String, MainConfigProperty> propKeys = new HashMap<>();

        //gives the names of the fields
        for (MainConfigProperty property : propertyList) {
            propKeys.put(property.getName(), property);

        }
        return propKeys;
    }

}
