package models;

import javax.validation.constraints.Size;

public class GloablPropertyDto {

    @Size(min = 5)
    private String propertyName;

    //@Size(min = 5)
    private String propertyValue;

    public GloablPropertyDto() {
        //default
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
}
