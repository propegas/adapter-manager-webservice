package models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TemplatePropertyDto {

    @Size(min = 5)
    public String adaptersDirectoryFullPath;

    @NotNull
    public String adapterDirectory;

    @NotNull
    public String adapterFileName;

    @NotNull
    @Pattern(regexp = "[aA-zZ_0-9-\\s]*", message = "Заголовок адаптера должно быть таким-то [aA-zZ_0-9-\\s]*")
    public String adapterTitle;

    @NotNull
    @Size(min = 6)
    @Pattern(regexp = "[aA-zZ_-]*", message = "Имя адаптера должно быть таким-то [aA-zZ_-]*")
    public String adapterName;

    @NotNull
    public String adapterDescription;

    @NotNull
    public String adapterShFileName;

    @NotNull
    public String errorLogFile;

    @NotNull
    public String mainLogFile;

    public TemplatePropertyDto() {}

}
