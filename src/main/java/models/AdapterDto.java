package models;

import javax.validation.constraints.Size;

public class AdapterDto {

    @Size(min = 5)
    public String title;
    
    @Size(min = 5)
    public String content;

    @Size(min = 5)
    public String jarFileName;

    @Size(min = 5)
    public String jarFilePath;

    @Size(min = 2)
    public String checkStatusCommands;

    @Size(min = 5)
    public String startCommands;

    @Size(min = 5)
    public String stopCommands;

    //@Size(min = 5)
    public String status = "Unknown";

    public String logFile = "";

    public String errorLogFile = "";

    public AdapterDto() {}

}
