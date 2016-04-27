package models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AdapterConfigFilePropertyDto {

    @Size(min = 5)
    private String propertyName;

    @NotNull
    private String propertyValue;

    @NotNull
    private String propertyLabel;

    public AdapterConfigFilePropertyDto() {
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

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }
}
