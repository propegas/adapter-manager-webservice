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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdapterConfigFileDao {

    static final String CONFIG_FILE_IDS_NOT_FOUND = "Adapter ID or ConfigFile ID not found in DB";
    static final Logger logger = LoggerFactory.getLogger(AdapterConfigFileDao.class);

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
    public boolean postConfigFile(Long adapterId, AdapterConfigFileDto configFileDto) {
        EntityManager entityManager = entityManagerProvider.get();

        Query query = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :adapIdParam");
        Adapter adapter = (Adapter) query.setParameter("adapIdParam", adapterId).getSingleResult();

        if (adapter == null) {
            String errorMessage = "Adapter ID or ConfigFile ID not found in DB";
            logger.error(errorMessage, new NoResultException());
            return false;
        }

        AdapterConfigFile configFile = new AdapterConfigFile(adapterId, configFileDto.configFile);
        configFile.setConfigDescription(configFileDto.getConfigDescription());

        entityManager.persist(configFile);

        return true;

    }

    @Transactional
    public boolean deleteConfigFile(Long adapterId, Long confId) {

        EntityManager entityManager = entityManagerProvider.get();

        if (adapterId != null) {

            Query getConfFileFromDb = entityManager.createQuery("SELECT x " +
                    "FROM AdapterConfigFile x " +
                    "WHERE x.id = :idParam " +
                    "AND x.adapterId = :adapIdParam");

            AdapterConfigFile adapterConfigFileDb = (AdapterConfigFile) getConfFileFromDb
                    .setParameter("idParam", confId)
                    .setParameter("adapIdParam", adapterId)
                    .getSingleResult();

            logger.debug(String.format("Remove Config File %d for Adapter %d", confId, adapterId));
            try {
                entityManager.remove(adapterConfigFileDb);
            } catch (IllegalArgumentException e) {
                logger.error("Error while removing ConfigFile entity from DB: ", e);
                return false;
            }
            return true;

        }

        logger.error("Error while removing ConfigFile entity from DB: adapterId not found");
        return false;
    }

    @Transactional
    public boolean saveConfigFile(Long adapterId,
                                  Long confId,
                                  AdapterConfigFileDto configFileDto) {

        AdapterConfigFile adapterConfigFileDb;
        EntityManager entityManager = entityManagerProvider.get();

        if (adapterId != null && confId != null) {

            Query getConfFileFromDb = entityManager.createQuery("" +
                    "SELECT x " +
                    "FROM AdapterConfigFile x " +
                    "WHERE x.id = :idParam " +
                    "AND x.adapterId = :adapIdParam");

            adapterConfigFileDb = (AdapterConfigFile) getConfFileFromDb
                    .setParameter("idParam", confId)
                    .setParameter("adapIdParam", adapterId)
                    .getSingleResult();

            Query selectAdapter = entityManager.createQuery("" +
                    "SELECT x FROM Adapter x WHERE x.id = :adapIdParam");
            Adapter adapter = (Adapter) selectAdapter
                    .setParameter("adapIdParam", adapterId)
                    .getSingleResult();

            if (adapter == null) {
                String errorMessage = CONFIG_FILE_IDS_NOT_FOUND;
                logger.error(errorMessage, new NoResultException());
                return false;
            }

            adapterConfigFileDb.setConfigFile(configFileDto.configFile);
            adapterConfigFileDb.setConfigDescription(configFileDto.getConfigDescription());
            entityManager.flush();
            entityManager.refresh(adapter);

            logger.info("Config File updated in DB.");
            return true;

        }

        String errorMessage = CONFIG_FILE_IDS_NOT_FOUND;
        logger.error(errorMessage, new NoResultException());
        return false;
    }
}
