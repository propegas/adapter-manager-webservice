package scheduler;

import camel.AdapterErrorEvents;
import camel.Heartbeats;
import com.devdaily.system.AdapterManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.AdapterDao;
import models.Adapter;
import models.AdaptersDto;
import ninja.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by vgoryachev on 06.04.2016.
 * Package: PACKAGE_NAME.
 */
@Singleton
public class ScheduledAction {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledAction.class);
    @Inject
    AdapterDao adapterDao;

    @Schedule(delay = 30, initialDelay = 15, timeUnit = TimeUnit.SECONDS)
    public void doStuffEach60Seconds() {

        logger.debug("Start adapter process checking...");

        AdaptersDto adapters = adapterDao.getAllAdapters();

        for (int i = 0; i < adapters.adapters.size(); i++) {
            Adapter adapter = adapters.adapters.get(i);
            System.out.println("======");

            String command = adapter.getCheckStatusCommands();
            System.out.println(command);

            String output = null;
            try {
                output = AdapterManager.executeCommand(command)[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(String.format("Output: ***%s***", output));

            if (null == output) {
                adapterDao.updateStatus(adapter.id, "Unknown");
            } else if (!output.toUpperCase().matches(".*(NOT FOUND|FAILED).*")
                    && !"".equals(output.trim())) {
                adapterDao.updateStatus(adapter.id, "Running");
            } else {
                adapterDao.updateStatus(adapter.id, "Stopped");
            }

            System.out.println(String.format("%s: %s", adapter.title, adapter.getStatus()));
            System.out.println("======");
        }
        //adapters.adapters.g
        // do stuff
    }

    @Schedule(delay = 30, initialDelay = 30, timeUnit = TimeUnit.SECONDS)
    public void doHeartbeatsAgeChecking() {

        logger.debug("*** adapterDao: " + adapterDao);

        logger.debug("Start raw Heartbeats age checking...");
        Heartbeats.checkAndUpdateHeartbeatsStatus();
        logger.debug("Stop raw Heartbeats age checking.");

        logger.debug("Start DB adapters heartbeats checking...");
        AdaptersDto adapters = adapterDao.getAllAdapters();
        for (int i = 0; i < adapters.adapters.size(); i++) {
            Adapter adapter = adapters.adapters.get(i);

            logger.debug(String.format("Checking adapter %s Heartbeat Status...", adapter.getName()));

            String status = Heartbeats.getHeartbeatStatusByName(adapter.getName());
            logger.debug(String.format("Set adapter %s (%d) heartbeat Status to '%s'",
                    adapter.getName(), adapter.id, status));
            adapterDao.updateHeartbeatStatus(adapter.id, status);
        }

    }

    @Schedule(delay = 10, initialDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void doAdapterEventsFlushing() {

        // delete all events from in-memory hash
        logger.debug("deleting all events from in-memory hash...");
        AdapterErrorEvents.emptyHash();
    }

}

