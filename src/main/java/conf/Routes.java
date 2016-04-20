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

package conf;

import controllers.AdapterController;
import controllers.ConfigFileController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

import controllers.ApiController;
import controllers.ApplicationController;
import controllers.LoginLogoutController;

public class Routes implements ApplicationRoutes {
    
    @Inject
    NinjaProperties ninjaProperties;

    /**
     * Using a (almost) nice DSL we can configure the router.
     * 
     * The second argument NinjaModuleDemoRouter contains all routes of a
     * submodule. By simply injecting it we activate the routes.
     * 
     * @param router
     *            The default router of this application
     */
    @Override
    public void init(Router router) {  
        
        // puts test data into db:
        if (!ninjaProperties.isProd()) {
            router.GET().route("/setup").with(ApplicationController.class, "setup");
        }
        
        ///////////////////////////////////////////////////////////////////////
        // Login / Logout
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/login").with(LoginLogoutController.class, "login");
        router.POST().route("/login").with(LoginLogoutController.class, "loginPost");
        router.GET().route("/logout").with(LoginLogoutController.class, "logout");
        
        ///////////////////////////////////////////////////////////////////////
        // Create new adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/new").with(AdapterController.class, "adapterNew");
        router.POST().route("/adapter/new").with(AdapterController.class, "adapterNewPost");

        ///////////////////////////////////////////////////////////////////////
        // Duplicate new adapter from existing
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/duplicate").with(AdapterController.class, "adapterDuplicate");
        //router.POST().route("/adapter/{id}/duplicate").with(AdapterController.class, "adapterNewPost");

        ///////////////////////////////////////////////////////////////////////
        // Edit adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/edit").with(AdapterController.class, "adapterEdit");
        router.POST().route("/adapter/{id}/edit").with(AdapterController.class, "adapterEditPost");
        
        ///////////////////////////////////////////////////////////////////////
        // Show adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}").with(AdapterController.class, "adapterShow");

        ///////////////////////////////////////////////////////////////////////
        // Delete adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/delete").with(AdapterController.class, "adapterDelete");
        router.POST().route("/adapter/{id}/delete").with(AdapterController.class, "adapterDeletePost");

        ///////////////////////////////////////////////////////////////////////
        // Stop adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/stop").with(AdapterController.class, "adapterStop");
        router.POST().route("/adapter/{id}/stop").with(AdapterController.class, "adapterStopPost");

        ///////////////////////////////////////////////////////////////////////
        // Start adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/start").with(AdapterController.class, "adapterStart");
        router.POST().route("/adapter/{id}/start").with(AdapterController.class, "adapterStartPost");

        ///////////////////////////////////////////////////////////////////////
        // Get logs from adapter
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/log").with(AdapterController.class, "adapterLog");
        //router.POST().route("/adapter/{id}/start").with(AdapterController.class, "adapterStartPost");

        ///////////////////////////////////////////////////////////////////////
        // Api for management of software
        ///////////////////////////////////////////////////////////////////////
        // main
        router.POST().route("/api/login").with(ApiController.class, "apiLoginPost");
        router.GET().route("/api/logout").with(ApiController.class, "apiLogout");
        router.GET().route("/api/auth/check").with(ApiController.class, "apiAuthCheck");
        router.GET().route("/api/adapters.json").with(ApiController.class, "getAdaptersJson");
        router.GET().route("/api/adapter/{id}.json").with(ApiController.class, "getAdapterJson");
        router.GET().route("/api/adapters.xml").with(ApiController.class, "getAdaptersXml");
        router.POST().route("/api/adapter.json").with(ApiController.class, "postAdapterJson");
        router.POST().route("/api/adapter.xml").with(ApiController.class, "postAdapterXml");
        router.GET().route("/api/adapter/{id}/log").with(ApiController.class, "getAdapterLogJson");
        // config files
        router.GET().route("/api/adapter/{id}/configfiles.json").with(ApiController.class, "getConfigFilesJson");
        router.GET().route("/api/adapter/{id}/configfile/{confid}.json").with(ApiController.class, "getConfigFileJson");
        router.POST().route("/api/adapter/{id}/configfile.json").with(ApiController.class, "postConfigFileJson");
        router.GET().route("/api/adapter/{id}/configfile/{confid}/getrawcontent.json").with(ApiController.class, "getConfigFileRawContentJson");
        router.POST().route("/api/adapter/{id}/configfile/{confid}/postrawcontent.json").with(ApiController.class, "postConfigFileRawContentJson");
        // adapter templates
        router.GET().route("/api/templates.json").with(ApiController.class, "getAdapterTemplatesJson");
        router.GET().route("/api/template/{id}.json").with(ApiController.class, "getAdapterTemplateJson");
        router.GET().route("/api/template/{id}/properties.json").with(ApiController.class, "getAdapterTemplatePropertiesJson");
        router.POST().route("/api/template/{id}/properties.json").with(ApiController.class, "postAdapterTemplatePropertiesJson");
        router.POST().route("/api/template/{id}/xmlfileid/{xmlfileid}/configfile/{confid}/properties.json")
                .with(ApiController.class, "postTemplateConfFilePropertiesJson");

        ///////////////////////////////////////////////////////////////////////
        // Show adapter config files
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfiles").with(ConfigFileController.class, "configFiles");

        ///////////////////////////////////////////////////////////////////////
        // New adapter config file
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfile/new").with(ConfigFileController.class, "configFileNew");
        router.POST().route("/adapter/{id}/configfile/new").with(ConfigFileController.class, "configFileNewPost");

        ///////////////////////////////////////////////////////////////////////
        // Get raw config file
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfile/{confid}/fileview").with(ConfigFileController.class, "configFileRawView");

        ///////////////////////////////////////////////////////////////////////
        // Edit and save raw config file
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfile/{confid}/fileedit").with(ConfigFileController.class, "configFileRawEdit");
        router.POST().route("/adapter/{id}/configfile/{confid}/fileedit").with(ConfigFileController.class, "configFileRawEditPost");

        ///////////////////////////////////////////////////////////////////////
        // Delete config file
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfile/{confid}/delete").with(ConfigFileController.class, "configFileDelete");
        router.POST().route("/adapter/{id}/configfile/{confid}/delete").with(ConfigFileController.class, "configFileDeletePost");

        ///////////////////////////////////////////////////////////////////////
        // Edit config file
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/adapter/{id}/configfile/{confid}/edit").with(ConfigFileController.class, "configFileEdit");
        router.POST().route("/adapter/{id}/configfile/{confid}/edit").with(ConfigFileController.class, "configFileEditPost");

        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////    
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
        
        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(ApplicationController.class, "index");
    }

}
