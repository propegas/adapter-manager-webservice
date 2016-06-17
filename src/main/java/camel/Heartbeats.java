package camel;

import com.google.inject.Inject;
import dao.AdapterDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vgoryachev on 02.06.2016.
 * Package: camel.
 */
public class Heartbeats {

    private static final Logger logger = LoggerFactory.getLogger(Heartbeats.class);

    private static Map<String, Object> heartbeatsArr = new HashMap<>();

    @Inject
    AdapterDao adapterDao;

    public static Map<String, Object> getHeartbeatsArr() {
        return heartbeatsArr;
    }

    public static void setHeartbeatsArr(Map<String, Object> heartbeatsArr) {
        Heartbeats.heartbeatsArr = heartbeatsArr;
    }

    public static void checkAndUpdateHeartbeatsStatus() {

        for (Map.Entry<String, Object> heartbeatHashEntry : heartbeatsArr.entrySet()) {
            String adapterName = heartbeatHashEntry.getKey();
            Heartbeat heartbeat = (Heartbeat) heartbeatHashEntry.getValue();

            long currentTime = System.currentTimeMillis() / 1000;
            long heartbeatTimestamp = heartbeat.getTimestamp();

            logger.debug(String.format("Compare with current Timestamp: %s", currentTime));
            logger.debug(String.format("Current Timestamp %d - heartbeatTimastamp %d = %d",
                    currentTime, heartbeatTimestamp, currentTime - heartbeatTimestamp));
            if (currentTime - heartbeatTimestamp > 60 * 5) {
                logger.debug(String.format("Set %s adapter heartbeat status from '%s' to 'not ok'",
                        adapterName, heartbeat.getHeartbeatStatus()));
                heartbeat.setHeartbeatStatus(Heartbeat.HeartbeatStatus.FAIL);
            } else {
                logger.debug(String.format("Set %s adapter heartbeat status from '%s' to 'ok'",
                        adapterName, heartbeat.getHeartbeatStatus()));
                heartbeat.setHeartbeatStatus(Heartbeat.HeartbeatStatus.OK);
            }

            logger.debug(String.format("Heartbeat from %s === %s", adapterName, heartbeat));

        }

    }

    public static String getHeartbeatStatusByName(String name) {
        Heartbeat heartbeat = (Heartbeat) heartbeatsArr.get(name);

        if (heartbeat == null) {
            logger.debug(String.format("Adapter with name %s not found in raw Heartbeats",
                    name));
            return String.valueOf(Heartbeat.HeartbeatStatus.UNKNOWN);
        } else {
            logger.debug(String.format("Adapter with name %s found in raw Heartbeats", name));
            logger.debug(String.format("Adapter's %s HeartbeatStatus is %s",
                    name, heartbeat.getHeartbeatStatus()));
            return heartbeat.getHeartbeatStatus();
        }

    }

    public void addHeartbeatMessage(Heartbeat heartbeat) {
        logger.debug(String.format("*** Add Heartbeat to HASH: %s %s",
                heartbeat.getAdapterName(), heartbeat.getTimestamp()));
        heartbeatsArr.put(heartbeat.getAdapterName(), heartbeat);
    }
}
