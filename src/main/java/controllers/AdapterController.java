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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.AdapterDao;
import etc.LoggedInUser;
import models.Adapter;
import models.AdapterDto;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;

import java.util.Map;

@Singleton
public class AdapterController {

    @Inject
    AdapterDao adapterDao;

    ///////////////////////////////////////////////////////////////////////////
    // Show adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterShow(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter).render("status", "test");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Create new adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterNew() {

        return Results.html();

    }

    @FilterWith(SecureFilter.class)
    public Result adapterNewPost(@LoggedInUser String username,
                                 Context context,
                                 @JSR303Validation AdapterDto adapterDto,
                                 Validation validation) {

        if (validation.hasViolations()) {

            context.getFlashScope().error("Please correct fields.");
            context.getFlashScope().put("title", adapterDto.title);
            context.getFlashScope().put("content", adapterDto.content);

            return Results.redirect("/adapter/new");

        } else {

            adapterDao.postAdapter(adapterDto);

            context.getFlashScope().success("New adapter created.");

            return Results.redirect("/");

        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Edit adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterEdit(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter);

    }

    @FilterWith(SecureFilter.class)
    public Result adapterEditPost(@PathParam("id") Long id,
                                  @LoggedInUser String username,
                                  Context context,
                                  @JSR303Validation AdapterDto adapterDto,
                                  Validation validation) {

        if (validation.hasViolations()) {

            context.getFlashScope().error("Please correct field.");
            context.getFlashScope().put("title", adapterDto.title);
            context.getFlashScope().put("content", adapterDto.content);

            return Results.redirect(String.format("/adapter/%d/edit", id));

        } else {

            adapterDao.saveAdapter(id, username, adapterDto);

            context.getFlashScope().success("Adapter saved.");

            return Results.redirect("/");

        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Duplicate adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterDuplicate(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter);

    }

    ///////////////////////////////////////////////////////////////////////////
    // Delete adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterDelete(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter).render("status", "test");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Delete adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterDeletePost(@PathParam("id") Long id,
                                    Context context) {

        Adapter adapter = null;

        if (id != null) {
            adapter = adapterDao.getAdapter(id);
        }

        adapterDao.deleteAdapter(id, adapter);

        context.getFlashScope().success("Adapter deleted.");

        return Results.redirect("/");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Stop adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterStop(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter).render("status", "test");

    }

    @FilterWith(SecureFilter.class)
    public Result adapterStopPost(@PathParam("id") Long id,
                                  Context context) {

        Adapter adapter = null;
        if (id != null)
            adapter = adapterDao.getAdapter(id);

        String error = null;
        Map stopResult = AdapterManager.stopAdapter(id, adapter);

        if ((boolean) stopResult.get("result"))
            context.getFlashScope().success("Adapter stopped: " + stopResult.get("text"));
        else {
            context.getFlashScope().error("Error while Adapter stopping");
            error = stopResult.get("text").toString();
        }

        return Results.html()
                .render("error", error)
                .render("adapter", adapter)
                .template("views/AdapterController/adapterStop.ftl.html");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Start adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterStart(@PathParam("id") Long id) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        return Results.html().render("adapter", adapter).render("status", "test");

    }

    @FilterWith(SecureFilter.class)
    public Result adapterStartPost(@PathParam("id") Long id,
                                   Context context) {

        Adapter adapter = null;

        if (id != null) {

            adapter = adapterDao.getAdapter(id);

        }

        String error = null;

        Map startResult = AdapterManager.startAdapter(id, adapter);

        if ((boolean) startResult.get("result"))
            context.getFlashScope().success("Adapter started." + startResult.get("text"));
        else {
            context.getFlashScope().error("Error while Adapter starting");
            error = startResult.get("text").toString();
        }


        return Results.html()
                .render("error", error)
                .render("adapter", adapter)
                .template("views/AdapterController/adapterStart.ftl.html");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Show log adapter
    ///////////////////////////////////////////////////////////////////////////
    @FilterWith(SecureFilter.class)
    public Result adapterLog(@PathParam("id") Long id,
                             @Param("size") int size,
                             @Param("type") String type,
                             Context context) {

        Adapter adapter = null;
        if (id != null) {
            adapter = adapterDao.getAdapter(id);
        }

        String log = "";
        String error = null;

        Map logResult = AdapterManager.showLogAdapter(id, adapter, size, type);

        if ((boolean) logResult.get("result")) {
            context.getFlashScope().success("Logs got successfully.\n");
            log = log + logResult.get("text");
        } else {
            context.getFlashScope().error("Error while getting Logs from Adapter");
            error = logResult.get("text").toString();
        }

        return Results.html().render("adapter", adapter).render("error", error).render("log", log);

    }
}
