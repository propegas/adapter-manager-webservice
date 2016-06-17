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
import ninja.NinjaTest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class LoginLogoutControllerTest extends NinjaTest {

    @Test
    public void testLogingLogout() {

        Map<String, String> headers = Maps.newHashMap();

        // /////////////////////////////////////////////////////////////////////
        // Test posting of adapter does not work without login
        // /////////////////////////////////////////////////////////////////////
        String response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "adapter/1/new", headers);
        System.out.println(response);
        assertTrue(response.contains("Error. Forbidden."));

        // /////////////////////////////////////////////////////////////////////
        // Login
        // /////////////////////////////////////////////////////////////////////
        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");

        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress()
                + "login", headers, formParameters);

        // /////////////////////////////////////////////////////////////////////
        // Test posting of adapter works when are logged in
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "adapter/1/new", headers);
        
        assertTrue(response.contains("New adapter"));

        // /////////////////////////////////////////////////////////////////////
        // Logout
        // /////////////////////////////////////////////////////////////////////
        ninjaTestBrowser.makeRequest(getServerAddress() + "logout", headers);

        // /////////////////////////////////////////////////////////////////////
        // Assert that posting of adapter does not work any more...
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "adapter/1/new", headers);
        System.out.println(response);
        assertTrue(response.contains("Error. Forbidden."));

    }

}
