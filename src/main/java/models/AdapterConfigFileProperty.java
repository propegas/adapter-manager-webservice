package models;

import javax.persistence.Column;
import javax.persistence.Embeddable;

//Entity(name = "AdapterConfigFile_keys")
@Embeddable
public class AdapterConfigFileProperty {

   // @Id
   // @GeneratedValue(strategy=GenerationType.IDENTITY)
    //@Column(name = "adapterconfigfile_id")
   // private Long confFileId;

    @Column(name = "propertyname")
    private String propertyName;
    @Column(name = "propertyvalue")
    private String propertyValue;
    @Column(name = "propertylabel")
    private String propertyLabel;

    public AdapterConfigFileProperty() {
// default constructor
    }

    public AdapterConfigFileProperty(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
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

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }
}