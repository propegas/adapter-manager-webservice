package camel;

/**
 * Created by vgoryachev on 03.06.2016.
 * Package: camel.
 */
public class Heartbeat {

    private String adapterName;
    private Long timestamp;
    private Long brokerInTimestamp;
    private String heartbeatStatus;

    public Heartbeat(String adapterName, long timestamp, long brokerInTimestamp) {
        this.adapterName = adapterName;
        this.timestamp = timestamp;
        this.brokerInTimestamp = brokerInTimestamp;
        this.heartbeatStatus = String.valueOf(HeartbeatStatus.UNKNOWN);
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getBrokerInTimestamp() {
        return brokerInTimestamp;
    }

    public void setBrokerInTimestamp(Long brokerInTimestamp) {
        this.brokerInTimestamp = brokerInTimestamp;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", this.getTimestamp(), this.getBrokerInTimestamp(), this.heartbeatStatus);
    }

    public String getHeartbeatStatus() {
        return heartbeatStatus;
    }

    public void setHeartbeatStatus(HeartbeatStatus heartbeatStatus) {
        this.heartbeatStatus = String.valueOf(heartbeatStatus);
    }

    public enum HeartbeatStatus {
        OK, FAIL, UNKNOWN;

        public static HeartbeatStatus fromValue(String v) {
            return valueOf(v);
        }

        public String value() {
            return name();
        }
    }
}
