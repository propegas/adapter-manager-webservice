package camel;

import com.google.inject.Inject;
import dao.AdapterDao;
import dao.AdapterEventDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vgoryachev on 02.06.2016.
 * Package: camel.
 */
@Singleton
public class AdapterErrorEvents {

    private static final Logger logger = LoggerFactory.getLogger(AdapterErrorEvents.class);

    private static Map<String, AdapterErrors> adapterErrorEventMessages = new HashMap<>();

    @Inject
    AdapterEventDao adapterEventDao;

    @Inject
    AdapterDao adapterDao;

    public static Map<String, AdapterErrors> getAdapterErrorEventMessages() {
        return adapterErrorEventMessages;
    }

    public static void setAdapterErrorEventMessages(Map<String, AdapterErrors> adapterErrorEventMessages) {
        AdapterErrorEvents.adapterErrorEventMessages = adapterErrorEventMessages;
    }

    public static void emptyHash() {
        AdapterErrorEvents.adapterErrorEventMessages = new HashMap<>();
    }

    public Map<String, Serializable> addErrorMessage(String eventSource, ErrorMessage errorEventMessage) {

        AdapterErrors errorEvent = adapterErrorEventMessages.get(eventSource);
        if (errorEvent == null) {
            errorEvent = new AdapterErrors();
        }

        List<ErrorMessage> savedAdapterErrorEventsList = errorEvent.getEventMessagesList();

        logger.debug(String.format("*** Check Error Event to HASH: %s %s",
                eventSource, errorEventMessage.getMessage()));

        logger.debug("********** Saved event messages size: " + savedAdapterErrorEventsList.size());

        Map<String, Serializable> map = new HashMap<>();

        boolean isMessageExist = false;
        for (ErrorMessage savedAdapterErrorEvent : savedAdapterErrorEventsList) {

            logger.debug("********** Saved event message: " + savedAdapterErrorEvent.getMessage());
            logger.debug("********** Saved event timestamp: " + savedAdapterErrorEvent.getTimestamp());
            logger.debug("********** Saved event count: " + savedAdapterErrorEvent.getRepeatCounter());

            String savedMessage = savedAdapterErrorEvent.getMessage();
            if (savedMessage.equals(errorEventMessage.getMessage())) {

                logger.debug("*** Update existed message...");
                isMessageExist = true;
                savedAdapterErrorEvent.setRepeatCounter(savedAdapterErrorEvent.getRepeatCounter() + 1);
                savedAdapterErrorEvent.setTimestamp(errorEventMessage.getTimestamp());
                map.put("repeatCounter", savedAdapterErrorEvent.getRepeatCounter());
                break;
            }

        }

        if (!isMessageExist) {
            logger.debug(String.format("*** Add Error Message to List: %s",
                    errorEventMessage.getMessage()));
            savedAdapterErrorEventsList.add(errorEventMessage);
        }

        logger.debug(String.format("*** Save and add List to Adapter: %s", eventSource));
        errorEvent.setEventMessagesList(savedAdapterErrorEventsList);
        adapterErrorEventMessages.put(eventSource, errorEvent);

        map.put("result", isMessageExist);

        return map;
    }

}
