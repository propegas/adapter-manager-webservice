/**
 * Copyright (C) 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import models.AdapterConfigFileDto;
import models.AdapterConfigFilesDto;
import models.AdapterDto;
import models.AdaptersDto;
import ninja.NinjaTest;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApiControllerTest extends NinjaTest {

    @Test
    public void testGetAndPostAdapterViaJson() throws Exception {

        // /////////////////////////////////////////////////////////////////////
        // Test initial data:
        // /////////////////////////////////////////////////////////////////////
        String response = ninjaTestBrowser.makeJsonRequest(getServerAddress()
                + "adapter-api/adapters");
        System.out.println("response: " + response);

        AdaptersDto adaptersDto = getGsonWithLongToDateParsing().fromJson(
                response, AdaptersDto.class);

        assertEquals(1, adaptersDto.adapters.size());

        // /////////////////////////////////////////////////////////////////////
        // Post new adapter:
        // /////////////////////////////////////////////////////////////////////
        AdapterDto adapterDto = new AdapterDto();
        adapterDto.content = "contentcontent";
        adapterDto.title = "new title new title";

        response = ninjaTestBrowser.postJson(getServerAddress()
                + "adapter-api/adapter", adapterDto);

        assertFalse(response.contains("Error. Forbidden."));

        doLogin();

        response = ninjaTestBrowser.postJson(getServerAddress()
                + "adapter-api/adapter", adapterDto);

        assertFalse(response.contains("Error. Forbidden."));

        // /////////////////////////////////////////////////////////////////////
        // Fetch adapters again => assert we got a new one ...
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeJsonRequest(getServerAddress()
                + "adapter-api/adapters");

        adaptersDto = getGsonWithLongToDateParsing().fromJson(response, AdaptersDto.class);
        // one new result:
        assertEquals(1, adaptersDto.adapters.size());

    }

    @Test
    public void testGetAndPostAdapterConfigFileViaJson() throws Exception {

        // /////////////////////////////////////////////////////////////////////
        // Test initial data:
        // /////////////////////////////////////////////////////////////////////
        String response = ninjaTestBrowser.makeJsonRequest(getServerAddress()
                + "adapter-api/adapter/1/configfiles");
        System.out.println("response: " + response);

        AdapterConfigFilesDto configFilesDto = getGsonWithLongToDateParsing().fromJson(
                response, AdapterConfigFilesDto.class);

        assertEquals(1, configFilesDto.adapterConfigFiles.size());

        // /////////////////////////////////////////////////////////////////////
        // Post new adapter config:
        // /////////////////////////////////////////////////////////////////////
        AdapterConfigFileDto configFileDto = new AdapterConfigFileDto();
        configFileDto.adapterId = (long) 1;
        configFileDto.configFile = "/test/test/123.conf";
        configFileDto.setConfigDescription("Описание");

        response = ninjaTestBrowser.postJson(getServerAddress()
                + "adapter-api/adapter/1/configfile", configFileDto);
        System.out.println("response: " + response);

        assertFalse(response.contains("Error. Forbidden."));

        doLogin();

        response = ninjaTestBrowser.postJson(getServerAddress()
                + "adapter-api/adapter/1/configfile", configFileDto);
        System.out.println("response: " + response);

        assertFalse(response.contains("Error. Forbidden."));

        //long confId = configFileDto.i

        // /////////////////////////////////////////////////////////////////////
        // Fetch configs again => assert we got a new one ...
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeJsonRequest(getServerAddress()
                + "adapter-api/adapter/1/configfiles");
        System.out.println("response: " + response);

        configFilesDto = getGsonWithLongToDateParsing().fromJson(response, AdapterConfigFilesDto.class);
        // one new result:
        assertEquals(3, configFilesDto.adapterConfigFiles.size());

        // /////////////////////////////////////////////////////////////////////
        // Fetch one config.
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeJsonRequest(getServerAddress()
                + "adapter-api/adapter/1/configfile/2");
        System.out.println("response: " + response);

        configFileDto = getGsonWithLongToDateParsing().fromJson(response, AdapterConfigFileDto.class);
        // one new result:
        assertTrue(response.contains("/test/test/123.conf"));

    }

    private Gson getGsonWithLongToDateParsing() {
        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json,
                                    Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        return gson;
    }

    private void doLogin() {

        Map<String, String> headers = Maps.newHashMap();

        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");

        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress()
                + "login", headers, formParameters);

    }

}
