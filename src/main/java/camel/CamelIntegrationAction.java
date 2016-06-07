package camel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.inject.Inject;
import dao.AdapterDao;
import dao.AdapterEventDao;
import models.Adapter;
import models.AdapterEvent;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.jms.ConnectionFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by vgoryachev on 02.06.2016.
 * Package: camel.
 */
@Singleton
public class CamelIntegrationAction {

    private static final Logger logger = LoggerFactory.getLogger(CamelIntegrationAction.class);
    private static final int CACHE_SIZE = 2500;
    private static final int MAX_FILE_SIZE = 512000;

    private static org.apache.camel.main.Main main = new org.apache.camel.main.Main();

    private static String activemqPort;
    private static String activemqIp;
    @Inject
    AdapterEventDao adapterEventDao;
    @Inject
    AdapterDao adapterDao;
    private NinjaProperties ninjaProperties;

    @Inject
    public CamelIntegrationAction(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Start(order = 90)
    public void startCamelService() throws Exception {

        if (!ninjaProperties.isTest()) {

            logger.debug("Camel Service Starting...");
            System.out.println("Camel Service Starting...");

            if (activemqPort == null || "".equals(activemqPort))
                activemqPort = "61616";
            if (activemqIp == null || "".equals(activemqIp))
                activemqIp = "172.20.19.195";

            logger.info("activemqIp: " + activemqIp);
            logger.info("activemqPort: " + activemqPort);


            main.addRouteBuilder(new IntegrationRoute());

            main.start();
        }
    }

    @Dispose(order = 90)
    public void stopCamelService() throws Exception {
        if (!ninjaProperties.isTest()) {
            main.stop();
        }
    }

    public class IntegrationRoute extends RouteBuilder {
        @Override
        public void configure() throws Exception {

            logger.info("Starting Custom Apache Camel component example");

            JsonDataFormat myJson = new JsonDataFormat();
            myJson.setLibrary(JsonLibrary.Jackson);

            myJson.setUnmarshalType(Event.class);
            myJson.setDisableFeatures(String.valueOf(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES));
            myJson.setDisableFeatures(String.valueOf(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));

            //PropertiesComponent properties = new PropertiesComponent();
            //properties.setLocation("classpath:zabbixapi.properties");
            //getContext().addComponent("properties", properties);

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "tcp://" + activemqIp + ":" + activemqPort
            );
            getContext().addComponent("activemq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

            File cachefile = new File("sendedEvents.dat");
            logger.info(String.format("Cache file created: %s", cachefile.createNewFile()));

            // Heartbeats
            from("activemq:Heartbeats.queue")

                    .log(LoggingLevel.INFO, "*** NEW HEARTBEAT BODY: ${body}")
                    .log(LoggingLevel.INFO, "*** NEW HEARTBEAT IN BODY: ${in.body}")
                    .log(LoggingLevel.INFO, "*** NEW HEARTBEAT OUT BODY: ${out.body}")
                    .to("file:heartbeats.out")
                    //.to("stream:out")
                    .log("HEARTBEAT: ${id}")
                    .unmarshal(myJson)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            processHeartbeatEvent(exchange);
                        }
                    });

            // Adpaters errors
            from("activemq:AdaptersErrors.queue")

                    .log(LoggingLevel.INFO, "*** NEW ERROR BODY: ${body}")
                    .log(LoggingLevel.INFO, "*** NEW ERROR IN BODY: ${in.body}")
                    .log(LoggingLevel.INFO, "*** NEW ERROR OUT BODY: ${out.body}")
                    .to("file:errors.out")
                    //.to("stream:out")
                    .log("ERROR: ${id}")
                    .unmarshal(myJson)
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            processErrorEvent(exchange);
                        }
                    });

        }

        private void processHeartbeatEvent(Exchange exchange) {

            logger.debug("********** Heartbeat Exchange: " + exchange);
            Event event = exchange.getIn().getBody(Event.class);
            long heartbeatTimestamp = (long) exchange.getIn().getHeader("timestamp", System.currentTimeMillis());
            long brokerInTime = (long) exchange.getIn().getHeader("brokerInTime", System.currentTimeMillis());
            logger.debug("********** Heartbeat Exchange body: " + event);
            logger.debug("********** Heartbeat Exchange body message: " + event.getMessage());
            logger.debug("********** Heartbeat Exchange body source: " + event.getEventsource());
            logger.debug("********** Heartbeat Exchange body timestamp: " + event.getTimestamp());
            logger.debug("********** Heartbeat HeartbeatExchange body eventCategory: " + event.getEventCategory());
            logger.debug("********** Heartbeat Exchange body object: " + event.getObject());
            logger.debug("********** Heartbeat Timestamp: " + heartbeatTimestamp);
            logger.debug("********** Heartbeat brokerInTime: " + brokerInTime);

            Heartbeat heartbeat = new Heartbeat(event.getEventsource(), event.getTimestamp(), brokerInTime);

            Heartbeats heartbeats = new Heartbeats();
            heartbeats.addHeartbeatMessage(heartbeat);

            Map<String, Object> arr = Heartbeats.getHeartbeatsArr();
            logger.debug("********** Heartbeat array size: " + arr.size());
            Heartbeat savedHeartbeat = (Heartbeat) arr.get(event.getEventsource());
            logger.debug("********** Heartbeat saved timestamp: " + savedHeartbeat.getTimestamp());

        }

        private void processErrorEvent(Exchange exchange) {

            logger.debug("******* 1 AdapterDao: " + adapterDao);
            logger.debug("******* 1 AdapterEventDao: " + adapterEventDao);

            logger.debug("********** Error Exchange: " + exchange);
            Event event = exchange.getIn().getBody(Event.class);
            long heartbeatTimestamp = (long) exchange.getIn().getHeader("timestamp", System.currentTimeMillis());
            long brokerInTime = (long) exchange.getIn().getHeader("brokerInTime", System.currentTimeMillis());
            logger.debug("********** Error Exchange body: " + event);
            logger.debug("********** Error Exchange body message: " + event.getMessage());
            logger.debug("********** Error Exchange body source: " + event.getEventsource());
            logger.debug("********** Error Exchange body timestamp: " + event.getTimestamp());
            logger.debug("********** Error Exchange body eventCategory: " + event.getEventCategory());
            logger.debug("********** Error Exchange body object: " + event.getObject());
            logger.debug("********** Error Timestamp: " + heartbeatTimestamp);
            logger.debug("********** Error brokerInTime: " + brokerInTime);

            // add event to adapter hash
            ErrorMessage errorEventMessage = new ErrorMessage(event.getTimestamp(), event.getMessage());

            AdapterErrorEvents adapterErrors = new AdapterErrorEvents();
            // add event to in-memory hash-array
            Map<String, Serializable> checkMessageExist = adapterErrors
                    .addErrorMessage(event.getEventsource(), errorEventMessage);

            Map<String, AdapterErrors> adapterErrorEventMessages = AdapterErrorEvents.getAdapterErrorEventMessages();
            logger.debug("********** All HASH Error array size: " + adapterErrorEventMessages.size());
            AdapterErrors savedAdapterErrors = adapterErrorEventMessages.get(event.getEventsource());
            List<ErrorMessage> messagesList = savedAdapterErrors.getEventMessagesList();
            logger.debug("********** This Adapter Error array size: " + messagesList.size());

            // add or update event to DB
            addEventToDb(event.getEventsource(), errorEventMessage, checkMessageExist);

        }

        private void addEventToDb(String eventSource, ErrorMessage errorEventMessage,
                                  Map<String, Serializable> isMessageExistResult) {
            // get adapter id by it's name
            Adapter adapterByName = null;
            try {
                adapterByName = adapterDao.getAdapterByName(eventSource);
            } catch (NoResultException e) {
                logger.error("Error while SQL execute: Adapter name not found", e);
            } catch (NonUniqueResultException e) {
                logger.error("Error while SQL execute: Adapter name not unique", e);
            }
            if (adapterByName != null) {

                // update counter for existed message
                if ((boolean) isMessageExistResult.get("result")) {
                    errorEventMessage.setRepeatCounter((int) isMessageExistResult.get("repeatCounter"));
                }

                logger.debug(String.format("*** Adapter with name %s found in DB, id: %d ",
                        eventSource, adapterByName.id));

                // check if message exist in DB
                List<AdapterEvent> eventList = adapterEventDao
                        .getEventByMessageAndAdapterId(errorEventMessage, adapterByName.id);
                // add or update entity to DB
                if (eventList != null && !eventList.isEmpty()) {
                    logger.debug("*** Update existed message in DB...");
                    adapterEventDao.updateExistedAdapterEvent(adapterByName.id, errorEventMessage);

                } else {
                    logger.debug("*** Add row to DB..." + adapterEventDao + adapterDao);
                    AdapterEvent bbb = adapterEventDao.postAdapterEvent(adapterByName.id, errorEventMessage);
                    logger.debug("*** added row id: " + bbb.id);
                }
            } else {
                logger.debug(String.format("*** Adapter with name %s not found in DB", eventSource));
            }
        }
    }


}
