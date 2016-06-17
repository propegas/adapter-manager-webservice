package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "Adapter")
public class AdapterShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;
    public String title;
    private String name;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "adapter_id")
    @JsonIgnore
    private List<AdapterEvent> adapterEvents;

    public AdapterShort() {

        this.adapterEvents = null;
    }

    public AdapterShort(String title) {
        this.title = title;
        this.adapterEvents = null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AdapterEvent> getAdapterEvents() {
        return adapterEvents;
    }

    public void setAdapterEvents(List<AdapterEvent> adapterEvents) {
        this.adapterEvents = adapterEvents;
    }
}