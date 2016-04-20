package models;

import java.util.Map;

public class TemplateConfigFilePropertiesDto {

    private Map<String, Object> confFileProperties;

    public Map<String, Object> getConfFileProperties() {
        return confFileProperties;
    }

    public void setConfFileProperties(Map<String, Object> confFileProperties) {
        this.confFileProperties = confFileProperties;
    }
}
