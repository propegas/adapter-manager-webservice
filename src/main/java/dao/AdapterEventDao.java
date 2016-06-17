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
import models.AdapterShortEvent;
import models.AdapterShortEventsDto;
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
    public AdapterShortEventsDto getAllEventsByAdapterId(Long adapterId,
                                                         int pageSize,
                                                         int pageNum,
                                                         String orderByField,
                                                         String order) {

        EntityManager entityManager = entityManagerProvider.get();
        String orderBy;
        switch (orderByField) {
            case "message":
                orderBy = "message";
                break;
            case "timestamp":
                orderBy = "timestamp";
                break;
            default:
                orderBy = "timestamp";
                break;
        }

        String orderDirection;
        switch (order.toUpperCase()) {
            case "ASC":
                orderDirection = "ASC";
                break;
            case "DESC":
                orderDirection = "DESC";
                break;
            default:
                orderDirection = "DESC";
                break;
        }

        TypedQuery<AdapterShortEvent> query = entityManager
                .createQuery("SELECT x FROM AdapterShortEvent x " +
                                "where x.adapterDetail.id = :adapterId " +
                                "order by x." + orderBy + " " + orderDirection,
                        AdapterShortEvent.class);
        List<AdapterShortEvent> adapterEventList = query
                .setParameter("adapterId", adapterId)
                .setFirstResult((pageNum - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        AdapterShortEventsDto adapterEventsDto = new AdapterShortEventsDto();
        adapterEventsDto.adapterEventList = adapterEventList;

        return adapterEventsDto;

    }


    @UnitOfWork
    public AdapterShortEventsDto getAllEvents(int pageSize,
                                              int pageNum,
                                              String orderByField,
                                              String order) {

        EntityManager entityManager = entityManagerProvider.get();
        String orderBy;
        switch (orderByField) {
            case "message":
                orderBy = "message";
                break;
            case "timestamp":
                orderBy = "timestamp";
                break;
            default:
                orderBy = "timestamp";
                break;
        }

        String orderDirection;
        switch (order.toUpperCase()) {
            case "ASC":
                orderDirection = "ASC";
                break;
            case "DESC":
                orderDirection = "DESC";
                break;
            default:
                orderDirection = "DESC";
                break;
        }

        TypedQuery<AdapterShortEvent> query = entityManager
                .createQuery("SELECT x FROM AdapterShortEvent x " +
                                "join x.adapterDetail adapter " +
                                "order by x." + orderBy + " " + orderDirection,
                        AdapterShortEvent.class);
        List<AdapterShortEvent> adapterEventList = query
                .setFirstResult((pageNum - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        //adapterEventList.get(0).getAdapterDetail().getName();

        AdapterShortEventsDto adapterEventsDto = new AdapterShortEventsDto();
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
                                "where x.adapterDetail.id = :paramId " +
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
    public AdapterEvent postAdapterEvent(Adapter adapterDb, ErrorMessage adapterEventDto) {

        if (adapterDb != null) {

            EntityManager entityManager = entityManagerProvider.get();


            //adapterDb.setAdapterEvents(new ArrayList<AdapterEvent>());
            //adapterDb.getAdapterEvents().add(adapterEvent);

            AdapterEvent adapterEvent = new AdapterEvent();
            adapterEvent.setRepeatCounter(adapterEventDto.getRepeatCounter());
            adapterEvent.setMessage(adapterEventDto.getMessage());
            adapterEvent.setTimestamp(adapterEventDto.getTimestamp());
            adapterEvent.setAdapterDetail(adapterDb);
            //adapterEvent.setAdapterId(adapterId);

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
                    "WHERE x.adapterDetail.id = :idParam " +
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
