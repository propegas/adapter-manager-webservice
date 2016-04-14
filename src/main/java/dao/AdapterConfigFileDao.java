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

package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.Adapter;
import models.AdapterConfigFile;
import models.AdapterConfigFileDto;
import models.AdapterConfigFilesDto;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdapterConfigFileDao {

    @Inject
    Provider<EntityManager> entityManagerProvider;

    @UnitOfWork
    public AdapterConfigFilesDto getAllConfigsByAdapterId(Long adapterId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterConfigFile> query = entityManager
                .createQuery("SELECT x FROM AdapterConfigFile x where x.adapterId = :adapterId",
                        AdapterConfigFile.class);
        List<AdapterConfigFile> configFiles = query.setParameter("adapterId", adapterId).getResultList();

        AdapterConfigFilesDto configFilesDto = new AdapterConfigFilesDto();
        configFilesDto.adapterConfigFiles = configFiles;

        return configFilesDto;

    }

    @UnitOfWork
    public List<AdapterConfigFile> getAllConfigsByAdapterIdForPage(Long adapterId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterConfigFile> query = entityManager
                .createQuery("SELECT x FROM AdapterConfigFile x where x.adapterId = :adapterId",
                        AdapterConfigFile.class);

        return query.setParameter("adapterId", adapterId)
                .setFirstResult(0).setMaxResults(25).getResultList();

    }

    @UnitOfWork
    public AdapterConfigFile getConfigFile(Long adapterId, Long id) {

        EntityManager entityManager = entityManagerProvider.get();

        Query q = entityManager.createQuery("SELECT x " +
                "FROM AdapterConfigFile x " +
                "WHERE x.id = :idParam " +
                "AND x.adapterId = :adapIdParam");

        return (AdapterConfigFile) q.setParameter("idParam", id)
                .setParameter("adapIdParam", adapterId)
                .getSingleResult();

    }

    @Transactional
    public boolean postConfigFile(Long id, AdapterConfigFileDto configFileDto) {
        EntityManager entityManager = entityManagerProvider.get();

        Query query = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :adapIdParam");
        Adapter adapter = (Adapter) query.setParameter("adapIdParam", id).getSingleResult();

        if (adapter == null) {
            return false;
        }

        AdapterConfigFile configFile = new AdapterConfigFile(id, configFileDto.configFile);
        configFile.setConfigDescription(configFileDto.configDescription);

        entityManager.persist(configFile);

        return true;

    }
}
