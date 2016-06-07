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

import camel.ErrorMessage;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.Adapter;
import models.AdapterEvent;
import models.AdapterEventsDto;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdapterEventDao {

    static final String CONFIG_FILE_IDS_NOT_FOUND = "Adapter ID or ConfigFile ID not found in DB";
    static final Logger logger = LoggerFactory.getLogger(AdapterEventDao.class);

    @Inject
    Provider<EntityManager> entityManagerProvider;

    @Inject
    AdapterDao adapterDao;

    @UnitOfWork
    public AdapterEventsDto getAllEventsByAdapterId(Long adapterId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterEvent> query = entityManager
                .createQuery("SELECT x FROM AdapterEvent x where x.adapterId = :adapterId",
                        AdapterEvent.class);
        List<AdapterEvent> adapterEventList = query.setParameter("adapterId", adapterId)
                .setFirstResult(0).setMaxResults(25).getResultList();
        AdapterEventsDto adapterEventsDto = new AdapterEventsDto();
        adapterEventsDto.adapterEventList = adapterEventList;

        return adapterEventsDto;

    }

    @UnitOfWork
    public AdapterEvent getEventById(Long id) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterEvent> query = entityManager
                .createQuery("SELECT x FROM AdapterEvent x where x.id = :paramId",
                        AdapterEvent.class);

        return query.setParameter("paramId", id)
                .setFirstResult(0).getSingleResult();

    }

    @UnitOfWork
    public List<AdapterEvent> getEventByMessageAndAdapterId(ErrorMessage message, Long adapterId) {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<AdapterEvent> query = entityManager
                .createQuery("SELECT x FROM AdapterEvent x " +
                                "where x.adapterId = :paramId " +
                                "and x.message = :paramMessage",
                        AdapterEvent.class);

        return query
                .setParameter("paramId", adapterId)
                .setParameter("paramMessage", message.getMessage())
                .getResultList();

    }

    @Transactional
    public boolean deleteAdapterEvent(Long id) {
        EntityManager entityManager = entityManagerProvider.get();

        if (id != null) {

            Query selectAdapter = entityManager.createQuery("SELECT x FROM AdapterEvent x WHERE x.id = :idParam");
            Adapter adapterDb = (Adapter) selectAdapter.setParameter("idParam", id).getSingleResult();

            logger.debug("Remove AdapterEvent: " + id);
            entityManager.remove(adapterDb);

            return true;

        }

        return false;
    }

    @Transactional
    public AdapterEvent postAdapterEvent(Long adapterId, ErrorMessage adapterEventDto) {

        if (adapterId != null) {

            EntityManager entityManager = entityManagerProvider.get();

            AdapterEvent adapterEvent = new AdapterEvent();
            adapterEvent.setRepeatCounter(adapterEventDto.getRepeatCounter());
            adapterEvent.setMessage(adapterEventDto.getMessage());
            adapterEvent.setTimestamp(adapterEventDto.getTimestamp());
            adapterEvent.setAdapterId(adapterId);
            entityManager.persist(adapterEvent);
            entityManager.flush();
            //entityManager.getProperties().get()

            return adapterEvent;
        }

        return null;

    }

    @Transactional
    public boolean updateExistedAdapterEvent(Long adapterId, ErrorMessage adapterEventDto) {

        AdapterEvent adapterEvent;

        // get adapter id by it's name
        if (adapterId != null) {
            EntityManager entityManager = entityManagerProvider.get();
            Query selectAdapter = entityManager.createQuery("SELECT x FROM AdapterEvent x " +
                    "WHERE x.adapterId = :idParam " +
                    "AND x.message = :messageParam");
            adapterEvent = (AdapterEvent) selectAdapter
                    .setParameter("idParam", adapterId)
                    .setParameter("messageParam", adapterEventDto.getMessage())
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();
            if (adapterEvent == null) {
                return false;
            }
            // update timestamp and count
            adapterEvent.setTimestamp(adapterEventDto.getTimestamp());
            // get count from existed row
            adapterEvent.setRepeatCounter(adapterEvent.getRepeatCounter() + 1);

            entityManager.flush();
            entityManager.refresh(adapterEvent);

            return true;
        } else
            return false;

    }

}