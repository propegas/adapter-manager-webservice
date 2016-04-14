package models;

import javax.validation.constraints.NotNull;

public class AdapterConfigFileDto {

    @NotNull
    public Long adapterId;

    @NotNull
    public String configFile;

    public String configDescription = "";

    public AdapterConfigFileDto() {}

}
