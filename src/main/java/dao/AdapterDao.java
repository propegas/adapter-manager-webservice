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
import models.AdapterDto;
import models.AdaptersDto;
import models.UserAuth;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class AdapterDao {

    static final Logger logger = LoggerFactory.getLogger(AdapterDao.class);

    @Inject
    Provider<EntityManager> entityManagerProvider;

    @UnitOfWork
    public AdaptersDto getAllAdapters() {

        EntityManager entityManager = entityManagerProvider.get();

        TypedQuery<Adapter> query = entityManager.createQuery("SELECT x FROM Adapter x", Adapter.class);
        List<Adapter> adapters = query.getResultList();

        AdaptersDto adaptersDto = new AdaptersDto();
        adaptersDto.adapters = adapters;

        return adaptersDto;

    }

    /*
    @UnitOfWork
    public Adapter getFirstAdapterForFrontPage() {

        EntityManager entityManager = entityManagerProvider.get();

        Query q = entityManager.createQuery("SELECT x FROM Adapter x ORDER BY x.postedAt DESC");

        return (Adapter) q.setMaxResults(1).getSingleResult();


    }
    */

    @UnitOfWork
    public List<Adapter> getOlderAdaptersForFrontPage() {

        EntityManager entityManager = entityManagerProvider.get();

        Query q = entityManager.createQuery("SELECT x FROM Adapter x ORDER BY x.title, x.postedAt DESC");

        return (List<Adapter>) q.setFirstResult(0).setMaxResults(25).getResultList();


    }

    @UnitOfWork
    public Adapter getAdapter(Long id) {

        EntityManager entityManager = entityManagerProvider.get();

        Query q = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :idParam");

        return (Adapter) q.setParameter("idParam", id).getSingleResult();


    }

    /**
     * Returns false if user cannot be found in database.
     */
    @Transactional
    public Adapter postAdapter(AdapterDto adapterDto) {

        EntityManager entityManager = entityManagerProvider.get();

        Adapter adapter = new Adapter(adapterDto.title, adapterDto.content, adapterDto.jarFileName);
        adapter.setStatus("Unknown");
        adapter.setJarFilePath(adapterDto.jarFilePath);
        adapter.setCheckStatusCommands(adapterDto.checkStatusCommands);
        adapter.setStartCommands(adapterDto.startCommands);
        adapter.setStopCommands(adapterDto.stopCommands);
        adapter.setLogFile(adapterDto.logFile);
        adapter.setErrorLogFile(adapterDto.errorLogFile);

        entityManager.persist(adapter);
        entityManager.flush();
        //entityManager.getProperties().get()

        return adapter;

    }

    /**
     * Returns false if user cannot be found in database.
     */
    @Transactional
    public boolean saveAdapter(Long id, String username, AdapterDto adapterDto) {

        Adapter adapter;
        EntityManager entityManager = entityManagerProvider.get();

        if (id != null) {

            Query selectAdapter = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :idParam");
            adapter = (Adapter) selectAdapter.setParameter("idParam", id).getSingleResult();

            Query selectUser = entityManager.createQuery("SELECT x FROM UserAuth x WHERE x.username = :usernameParam");
            UserAuth userAuth = (UserAuth) selectUser.setParameter("usernameParam", username).getSingleResult();

            if (userAuth == null) {
                return false;
            }

            adapter.title = adapterDto.title;
            adapter.setCheckStatusCommands(adapterDto.checkStatusCommands);
            adapter.setStartCommands(adapterDto.startCommands);
            adapter.setStopCommands(adapterDto.stopCommands);
            adapter.setLogFile(adapterDto.logFile);
            adapter.setErrorLogFile(adapterDto.errorLogFile);
            adapter.content = adapterDto.content;
            adapter.setJarFilePath(adapterDto.jarFilePath);
            adapter.jarFileName = adapterDto.jarFileName;

            entityManager.flush();
            entityManager.refresh(adapter);

            return true;
        } else
            return false;

    }

    @Transactional
    public boolean updateStatus(Long id, String newStatus) {

        Adapter adapter;
        EntityManager entityManager = entityManagerProvider.get();

        if (id != null) {

            Query selectAdapter = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :idParam");
            adapter = (Adapter) selectAdapter.setParameter("idParam", id).getSingleResult();

            logger.debug("Updating status for adapter " + id);

            adapter.setStatus(newStatus);
            entityManager.flush();
            entityManager.refresh(adapter);
            return true;
        }

        return false;

    }

    @Transactional
    public void deleteAdapter(Long id, Adapter adapter) {
        EntityManager entityManager = entityManagerProvider.get();

        if (id != null) {

            Query selectAdapter = entityManager.createQuery("SELECT x FROM Adapter x WHERE x.id = :idParam");
            Adapter adapterDb = (Adapter) selectAdapter.setParameter("idParam", id).getSingleResult();

            logger.debug("Remove Adapter: " + id + " Title: " + adapter.title);
            entityManager.remove(adapterDb);

        }
    }

}
