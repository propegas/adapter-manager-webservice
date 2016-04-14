package scheduler;

import com.devdaily.system.AdapterManager;
//import adaper.executeCommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.AdapterDao;
import models.Adapter;
import models.AdaptersDto;
import ninja.scheduler.Schedule;

//import java.util.ArrayList;
//import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by vgoryachev on 06.04.2016.
 * Package: PACKAGE_NAME.
 */
@Singleton
public class ScheduledAction {
    @Inject
    AdapterDao adapterDao;

    @Schedule(delay = 60, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void doStuffEach60Seconds() {
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

            if (null == output ) {
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


}

