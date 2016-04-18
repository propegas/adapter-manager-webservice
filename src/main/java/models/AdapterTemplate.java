package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AdapterTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;
    private String templateXmlPath;

    public AdapterTemplate() {
// default constructor
    }

    public AdapterTemplate(String templateName, String templateXmlPath) {
        this.templateName = templateName;
        this.templateXmlPath = templateXmlPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateXmlPath() {
        return templateXmlPath;
    }

    public void setTemplateXmlPath(String templateXmlPath) {
        this.templateXmlPath = templateXmlPath;
    }
}