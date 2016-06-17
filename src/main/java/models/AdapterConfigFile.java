package models;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AdapterConfigFile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adapter_id")
    private Long adapterId;
    private String configFile;
    private String configDescription;

    @ElementCollection
    @CollectionTable(name = "AdapterConfigFile_keys", joinColumns = @JoinColumn(name = "AdapterConfigFile_id"))
    private List<AdapterConfigFileProperty> configFilePropertyList = new ArrayList<>();

    private String configFileXmlId;

    public AdapterConfigFile() {
// default constructor
        this.id = null;
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

    public List<AdapterConfigFileProperty> getConfigFilePropertyList() {
        return configFilePropertyList;
    }

    public void setConfigFilePropertyList(List<AdapterConfigFileProperty> configFilePropertyList) {
        this.configFilePropertyList = configFilePropertyList;
    }

    public String getConfigFileXmlId() {
        return configFileXmlId;
    }

    public void setConfigFileXmlId(String configFileXmlId) {
        this.configFileXmlId = configFileXmlId;
    }
}