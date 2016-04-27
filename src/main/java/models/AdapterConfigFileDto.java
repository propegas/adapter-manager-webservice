package models;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class AdapterConfigFileDto {

    @NotNull
    public Long adapterId;

    @NotNull
    public String configFile;

    private String configDescription = "";

    private Map<String, AdapterConfigFileProperty> confFileProperties;

    public AdapterConfigFileDto() {
        //default
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    public Map<String, AdapterConfigFileProperty> getConfFileProperties() {
        return confFileProperties;
    }

    public void setConfFileProperties(Map<String, AdapterConfigFileProperty> confFileProperties) {
        this.confFileProperties = confFileProperties;
    }
}
