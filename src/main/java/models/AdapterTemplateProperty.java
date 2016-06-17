package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "TemplateProperty")
public class AdapterTemplateProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String propertyName;
    private String propertyValue;
    private String createdXmlFileId;

    @Column(name = "Adapter_id")
    private Long adapterId;

    public AdapterTemplateProperty() {
// default constructor
    }

    public AdapterTemplateProperty(String templateName, String templateXmlPath, String createdXmlFileId) {
        this.propertyName = templateName;
        this.propertyValue = templateXmlPath;
        this.createdXmlFileId = createdXmlFileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }


    public String getCreatedXmlFileId() {
        return createdXmlFileId;
    }

    public void setCreatedXmlFileId(String createdXmlFileId) {
        this.createdXmlFileId = createdXmlFileId;
    }

    public Long getAdapterId() {
        return adapterId;
    }

    public void setAdapterId(Long adapterId) {
        this.adapterId = adapterId;
    }
}