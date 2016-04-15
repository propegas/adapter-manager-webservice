package models;

import javax.validation.constraints.NotNull;

public class AdapterConfigFileDto {

    @NotNull
    public Long adapterId;

    @NotNull
    public String configFile;

    private String configDescription = "";

    public AdapterConfigFileDto() {
        //default
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }
}
