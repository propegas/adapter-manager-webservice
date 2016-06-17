package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "adapterevent")
public class AdapterShortEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    //@Column(name = "adapter_id")
    //private Long adapterId;

    @Column(columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String message;
    private int repeatCounter;

    @ManyToOne()
    @JoinColumn(name = "adapter_id", insertable = false, updatable = false,
            nullable = false)
    private AdapterShort adapterDetail;

    public AdapterShortEvent() {

    }

    public AdapterShortEvent(Long timestamp, String message) {
        this.timestamp = new Date(timestamp * 1000);
        this.message = message;
        this.repeatCounter = 0;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = new Date(timestamp * 1000);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /*
    public Long getAdapterId() {

        return adapterId;
    }

    //public void setAdapterId(Long adapterId) {
        this.adapterId = adapterId;
    }

*/
    public int getRepeatCounter() {
        return repeatCounter;
    }

    public void setRepeatCounter(int repeatCounter) {
        this.repeatCounter = repeatCounter;
    }

    public AdapterShort getAdapterDetail() {
        return adapterDetail;
    }

    public void setAdapterDetail(AdapterShort adapterDetail) {
        this.adapterDetail = adapterDetail;
    }

}