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
import models.AdapterTemplateProperty;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import templates.Property;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdapterTemplatePropertyDao {

    static final String CONFIG_FILE_IDS_NOT_FOUND = "Adapter ID or ConfigFile ID not found in DB";
    static final Logger logger = LoggerFactory.getLogger(AdapterTemplatePropertyDao.class);

    @Inject
    Provider<EntityManager> entityManagerProvider;

    @UnitOfWork
    public List<AdapterTemplateProperty> getAllProperies() {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterTemplateProperty> query = entityManager
                .createQuery("SELECT x FROM TemplateProperty x",
                        AdapterTemplateProperty.class);

        return query.setFirstResult(0).getResultList();

    }

    @UnitOfWork
    public List<AdapterTemplateProperty> getPropertiesByNameAndValue(String propertyName, String propertyValue) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterTemplateProperty> query = entityManager
                .createQuery("SELECT x FROM TemplateProperty x " +
                                "where x.propertyName = :propertyNameParam " +
                                "and x.propertyValue = :propertyValueParam",
                        AdapterTemplateProperty.class);

        return query
                .setParameter("propertyNameParam", propertyName)
                .setParameter("propertyValueParam", propertyValue)
                .getResultList();

    }

    @UnitOfWork
    public List<AdapterTemplateProperty> getPropertiesByAdapterId(Long adapterId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterTemplateProperty> query = entityManager
                .createQuery("SELECT x FROM TemplateProperty x " +
                                "where x.adapterId = :adapterIdParam ",
                        AdapterTemplateProperty.class);

        return query
                .setParameter("adapterIdParam", adapterId)
                .getResultList();

    }

    @UnitOfWork
    public AdapterTemplateProperty getPropertiesByAdapterIdAndPropertyName(Long adapterId, String propertyName) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterTemplateProperty> query = entityManager
                .createQuery("SELECT x FROM TemplateProperty x " +
                                "where x.adapterId = :adapterIdParam " +
                                "and  x.propertyName = :propertyNameParam",
                        AdapterTemplateProperty.class);

        try {
            return query
                    .setParameter("adapterIdParam", adapterId)
                    .setParameter("propertyNameParam", propertyName)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Property not found in DB", e);
            return null;
        }

    }

    @Transactional
    public boolean postProperties(String xmlFileId, List<Property> properties, Long adapterId) {
        EntityManager entityManager = entityManagerProvider.get();

        if (properties.isEmpty()) {
            String errorMessage = "Properties List is empty";
            logger.error(errorMessage, new NoResultException());
            return false;
        }

        for (Property property : properties) {
            AdapterTemplateProperty templateProperty = new AdapterTemplateProperty(property.getName(), property.getValue(), xmlFileId);
            templateProperty.setAdapterId(adapterId);
            entityManager.persist(templateProperty);
        }

        return true;
    }
}
