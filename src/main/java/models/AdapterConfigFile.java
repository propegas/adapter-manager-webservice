package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AdapterConfigFile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adapter_id")
    private Long adapterId;
    private String configFile;
    private String configDescription;

    public AdapterConfigFile() {
// default constructor
    }

    public AdapterConfigFile(Long adapterId, String configFile) {
        this.adapterId = adapterId;
        this.configFile = configFile;
        this.configDescription = "";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdapterId() {
        return adapterId;
    }

    public void setAdapterId(Long adapterId) {
        this.adapterId = adapterId;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDecription) {
        this.configDescription = configDecription;
    }
}