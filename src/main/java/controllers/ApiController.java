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
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import templates.ConfProperty;
import templates.ConfigFile;
import templates.Property;
import templates.Template;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
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

    //@FilterWith(SecureFilter.class)
    public Result getAdaptersJson() {

        AdaptersDto adaptersDto = adapterDao.getAllAdapters();

        return Results.json().render(adaptersDto);

    }

    @FilterWith(SecureFilter.class)
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

    @FilterWith(SecureFilter.class)
    public Result getAdapterJson(@PathParam("id") Long id) {

        Adapter adapter = adapterDao.getAdapter(id);

        return Results.json().render(adapter);

    }

    @FilterWith(SecureFilter.class)
    public Result postAdapterJson(@LoggedInUser String username,
                                  AdapterDto adapterDto) {

        boolean succeeded = adapterDao.postAdapter(username, adapterDto);

        if (!succeeded) {
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");
        } else {
            return Results.json().render(RESULT_FIELD_NAME, "Success");
        }

    }

    @FilterWith(SecureFilter.class)
    public Result postAdapterXml(@LoggedInUser String username,
                                 AdapterDto adapterDto) {

        boolean succeeded = adapterDao.postAdapter(username, adapterDto);

        if (!succeeded) {
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
    @FilterWith(SecureFilter.class)
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

        return Results.json().render(adapterConfigFilesDto);

    }

    @FilterWith(SecureFilter.class)
    public Result getConfigFileJson(@PathParam("id") Long adapterId,
                                    @PathParam("confid") Long confId) {

        AdapterConfigFile configFile = configFileDao.getConfigFile(adapterId, confId);

        return Results.json().render(configFile);

    }

    @FilterWith(SecureFilter.class)
    public Result postConfigFileJson(@PathParam("id") Long adapterId,
                                     AdapterConfigFileDto configFileDto) {

        logger.debug("[TEST] configFileDto: " + configFileDto);
        logger.debug("[TEST] adapterId: " + adapterId);

        boolean succeeded = configFileDao.postConfigFile(adapterId, configFileDto);

        if (!succeeded) {
            return Results.notFound().render(RESULT_FIELD_NAME, "Error");
        } else {
            return Results.json().render(RESULT_FIELD_NAME, "Success");
        }

    }

    ////////////////////////////////////////////////////////////////////////
    // Get Config File raw content
    ////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
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

    @FilterWith(SecureFilter.class)
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

            return Results.notFound().json()
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

        // check for unique
        for (Property property : properties) {
            System.out.println("property: " + property);
            if (property.isUnique()) {
                System.out.println("property name: " + property.getName());
                System.out.println("property value: " + property.getValue());
                List<AdapterTemplateProperty> a = adapterTemplatePropertyDao
                        .getPropertiesByNameAndValue(property.getName(), property.getValue());
                if (!a.isEmpty()) {
                    return Results.badRequest().json()
                            .render(RESULT_FIELD_NAME, "Error")
                            .render("Message", String.format("Поле '%s' должно быть уникальным в системе", property.getLabel()));
                }
            }
        }

        // create XML string from object
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

            // save Properties in DB
            adapterTemplatePropertyDao.postProperties(xmlFileId, properties);

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

    private String xmlTemplateToStringWithGlobalProperties(Template template) {
        String xmlString;

        List<GlobalProperty> globalProperties = globalPropertyDao.getAllProperies();

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

        // get conffile bu id from xml
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
                .render(RESULT_FIELD_NAME, "Success")
                .render("configFile", configFile);
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
            /*
            m.setProperty( "com.sun.xml.internal.bind.characterEscapeHandler", new CharacterEscapeHandler() {
                @Override
                public void escape( char[] ac, int i, int j, boolean flag, Writer writer ) throws IOException
                {
                    // do not escape
                    writer.write( ac, i, j );
                }
            });
            */

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

    private HashMap<String, String> initPropertiesMapFromList(TemplatePropertyDto templatePropertyDto) throws IllegalAccessException {

        Field[] fields = TemplatePropertyDto.class.getDeclaredFields();
        HashMap<String, String> propKeys = new HashMap<>();
        for (Field field : fields) {
            //gives the names of the fields
            for (Property property : properties) {
                if (property.getName().equals(field.getName())) {
                    property.setValue((String) field.get(templatePropertyDto));
                    propKeys.put(property.getName(), property.getValue());
                }
            }
        }
        return propKeys;
    }

}
