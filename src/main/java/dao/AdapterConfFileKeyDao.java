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
import models.AdapterConfigFileProperty;
import models.AdapterConfigFilePropertyDto;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;

public class AdapterConfFileKeyDao {

    static final String CONFIG_FILE_IDS_NOT_FOUND = "Adapter ID or ConfigFile ID not found in DB";
    static final Logger logger = LoggerFactory.getLogger(AdapterConfFileKeyDao.class);

    @Inject
    Provider<EntityManager> entityManagerProvider;
/*

    @UnitOfWork
    public List<AdapterConfigFileProperty> getAllPropertiesByConfFileId(Long confFileId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterConfigFileProperty> query = entityManager
                .createQuery("SELECT x FROM AdapterConfigFile_keys x " +
                        "where x.confFileId = :confFileId ",
                        AdapterConfigFileProperty.class);

        return query
                .setParameter("confFileId", confFileId)
                .setFirstResult(0).getResultList();

    }

*/

}
