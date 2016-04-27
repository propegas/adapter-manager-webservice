package models;

import java.util.Map;

public class AdapterConfigFileSaveDto {

    private Map<String, AdapterConfigFileProperty> confFileProperties;

    public AdapterConfigFileSaveDto() {
        //default
    }

    public Map<String, AdapterConfigFileProperty> getConfFileProperties() {
        return confFileProperties;
    }

    public void setConfFileProperties(Map<String, AdapterConfigFileProperty> confFileProperties) {
        this.confFileProperties = confFileProperties;
    }
}
