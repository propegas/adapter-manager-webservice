/**
 * Copyright (C) 2013 the original author or authors.
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

import dao.AdapterConfigFileDao;
import dao.AdapterDao;
import models.Adapter;
import models.AdapterConfigFile;
import ninja.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiControllerMockTest {

    @Mock
    AdapterDao adapterDao;

    @Mock
    AdapterConfigFileDao configFileDao;
    
    ApiController apiController;
    
    @Before
    public void setupTest() {
        apiController = new ApiController();
        apiController.adapterDao = adapterDao;
        apiController.configFileDao = configFileDao;
        
    }
    

    @Test
    public void testThatPostAdapterReturnsOkWhenAdapterDaoReturnsTrue() {

        Adapter adapter = mock(Adapter.class);
        adapter.id = 123L;
        when(adapterDao.postAdapter(null)).thenReturn(adapter);
        
        Result result = apiController.postAdapterJson(null, null);
        
        assertEquals(200, result.getStatusCode());

    }
    
    @Test
    public void testThatPostAdapterReturnsNotFoundWhenAdapterDaoReturnsFalse() {

        Adapter adapter = mock(Adapter.class);
        when(adapterDao.postAdapter(null)).thenReturn(adapter);

        Result result = apiController.postAdapterJson(null, null);
        
        assertEquals(404, result.getStatusCode());

    }

    @Test
    public void testThatPostConfigFileReturnsOkWhenConfigFileDaoReturnsTrue() {
        AdapterConfigFile adapterConfigFile = mock(AdapterConfigFile.class);
        adapterConfigFile.setId(123L);
       //verify(adapterConfigFile).setId(1L);

        when(configFileDao.postConfigFile(null, null)).thenReturn(adapterConfigFile);

        Result result = apiController.postConfigFileJson(null, null);

        assertEquals(200, result.getStatusCode());

    }

    @Test
    public void testThatPostConfigFileReturnsNotFoundWhenConfigFileDaoReturnsFalse() {
        //AdapterConfigFile adapterConfigFile = mock(AdapterConfigFile.class);
        //adapterConfigFile.setId(null);
        //verify(adapterConfigFile).setId(null);
        //when(configFileDao.postConfigFile(null, null)).thenReturn(adapterConfigFile);

        //Result result = apiController.postConfigFileJson(null, null);

        //assertEquals(404, result.getStatusCode());

    }

}
