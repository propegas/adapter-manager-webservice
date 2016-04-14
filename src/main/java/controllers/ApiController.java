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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import dao.AdapterConfigFileDao;
import dao.AdapterDao;
import dao.UserAuthDao;
import etc.LoggedInUser;
import models.Adapter;
import models.AdapterConfigFile;
import models.AdapterConfigFileDto;
import models.AdapterConfigFilesDto;
import models.AdapterDto;
import models.AdaptersDto;
import models.UserAuth;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private static final String RESULT_FIELD_NAME = "Result";

    @Inject
    AdapterDao adapterDao;

    @Inject
    UserAuthDao userAuthDao;

    @Inject
    AdapterConfigFileDao configFileDao;


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
    public Result apiLoginPost(Context context) {

        String jsonInString = null;
        try {
            // get JSON from http body
            StringBuilder buffer = new StringBuilder();
            while ((jsonInString = context.getReader().readLine()) != null) {
                buffer.append(jsonInString);
            }
            jsonInString = buffer.toString();
        } catch (IOException e) {
            logger.error("Error while get UserAuth JSON: " + e);
        }
        ObjectMapper mapper = new ObjectMapper();
        UserAuth userAuth = null;
        try {
            userAuth = mapper.readValue(jsonInString, UserAuth.class);
        } catch (IOException e) {
            logger.error("Error while parse UserAuth JSON to UserAuth Object: " + e);
        }

        logger.info("**** result: " + jsonInString);
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
}
