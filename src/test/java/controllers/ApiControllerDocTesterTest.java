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
import models.Adapter;
import models.AdapterDto;
import models.AdaptersDto;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class ApiControllerDocTesterTest extends NinjaDocTester {
    
    String GET_ADAPTERS_URL = "/api/adapters.json";
    String GET_ADAPTER_URL = "/api/adapter/{id}.json";
    String POST_ADAPTER_URL = "/api/adapter.json";
    String LOGIN_URL = "/login";
    
    String USER = "bob@gmail.com";

    @Test
    public void testGetAndPostAdapterViaJson() {

        // /////////////////////////////////////////////////////////////////////
        // Test initial data:
        // /////////////////////////////////////////////////////////////////////
        
        sayNextSection("Retrieving adapters for a user (Json)");
        
        say("Retrieving all adapters of a user is a GET request to " + GET_ADAPTERS_URL);

        // GET_ADAPTERS_URL.replace("{username}", "bob@gmail.com")
        Response response = sayAndMakeRequest(
                Request.GET().url(
                        testServerUrl().path(GET_ADAPTERS_URL)));

        AdaptersDto adaptersDto = getGsonWithLongToDateParsing().fromJson(response.payload, AdaptersDto.class);


        sayAndAssertThat("We get back all 1 adapters of that user",
                adaptersDto.adapters.size(),
                CoreMatchers.is(1));

        // /////////////////////////////////////////////////////////////////////
        // Post new adapter:
        // /////////////////////////////////////////////////////////////////////
        sayNextSection("Posting new adapter (Json)");
        
        say("Posting a new adapter is a post request to " + POST_ADAPTER_URL);
        say("Please note that you have to be authenticated in order to be allowed to post.");
        
        AdapterDto adapterDto = new AdapterDto();
        adapterDto.content = "contentcontent";
        adapterDto.title = "new title new title";

        response = sayAndMakeRequest(
                Request.POST().url(
                    testServerUrl().path(POST_ADAPTER_URL))
                .payload(adapterDto));
        
        sayAndAssertThat(
                "You have to be authenticated in order to post adapters"
                , response.httpStatus 
                , CoreMatchers.is(403));
        
        doLogin();

        say("Now we are authenticated and expect the post to succeed...");
        response = sayAndMakeRequest(Request.POST().url(
                testServerUrl().path(POST_ADAPTER_URL))
                .contentTypeApplicationJson()
                .payload(adapterDto));

        sayAndAssertThat("After successful login we are able to post adapters"
                , response.httpStatus
                , CoreMatchers.is(200));

        // /////////////////////////////////////////////////////////////////////
        // Fetch adapters again => assert we got a new one ...
        // /////////////////////////////////////////////////////////////////////
        
        say("If we now fetch the adapters again we are getting a new adapter (the one we have posted successfully");
        response = sayAndMakeRequest(Request.GET().url(testServerUrl().path(GET_ADAPTERS_URL)));

        adaptersDto = getGsonWithLongToDateParsing().fromJson(response.payload, AdaptersDto.class);
        // one new result:
        sayAndAssertThat("We are now getting 2 adapters."
                , adaptersDto.adapters.size()
                , CoreMatchers.is(2));
        
        
        
        // /////////////////////////////////////////////////////////////////////
        // Fetch single adapter
        // /////////////////////////////////////////////////////////////////////
        say("We can also fetch an individual adapter via the Json Api.");
        say("That's a GET request to: " + GET_ADAPTER_URL);
        response = sayAndMakeRequest(
                Request.GET().url(
                        testServerUrl().path(
                                GET_ADAPTER_URL
                                        //.replace("{username}", "bob@gmail.com")
                                        .replace("{id}", "1"))));

        Adapter adapter = getGsonWithLongToDateParsing().fromJson(response.payload, Adapter.class);
        // one new result:
        sayAndAssertThat("And we got back the first adapter"
                , adapter.id
                , CoreMatchers.is(1L));
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

        say("To authenticate we send our credentials to " + LOGIN_URL);
        say("We are then issued a cookie from the server that authenticates us in further requests");

        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");
        
        makeRequest(
                Request.POST().url(
                    testServerUrl().path(LOGIN_URL))
                .addFormParameter("username", "bob@gmail.com")
                .addFormParameter("password", "secret"));
         }

}
