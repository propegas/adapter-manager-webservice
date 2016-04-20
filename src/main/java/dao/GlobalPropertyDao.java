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
import models.GlobalProperty;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class GlobalPropertyDao {

    static final String CONFIG_FILE_IDS_NOT_FOUND = "Adapter ID or ConfigFile ID not found in DB";
    static final Logger logger = LoggerFactory.getLogger(GlobalPropertyDao.class);

    @Inject
    Provider<EntityManager> entityManagerProvider;

    @UnitOfWork
    public List<GlobalProperty> getAllProperies() {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<GlobalProperty> query = entityManager
                .createQuery("SELECT x FROM GlobalConfig x",
                        GlobalProperty.class);

        return query.setFirstResult(0).getResultList();

    }

    @UnitOfWork
    public List<GlobalProperty> getProperiesByNameAndValue(String propertyName, String propertyValue) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<GlobalProperty> query = entityManager
                .createQuery("SELECT x FROM GlobalConfig x " +
                        "where x.propertyName = :propertyNameParam " +
                        "and x.propertyValue = :propertyValueParam",
                        GlobalProperty.class);

        return query
                .setParameter("propertyNameParam", propertyName)
                .setParameter("propertyValueParam", propertyValue)
                .getResultList();

    }

    @UnitOfWork
    public List<GlobalProperty> getProperiesByName(String propertyName) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<GlobalProperty> query = entityManager
                .createQuery("SELECT x FROM GlobalConfig x " +
                                "where x.propertyName = :propertyNameParam ",
                        GlobalProperty.class);

        return query
                .setParameter("propertyNameParam", propertyName)
                .getResultList();

    }

}
