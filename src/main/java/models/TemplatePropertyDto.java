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
    public String adapterTitle;

    @NotNull
    @Pattern(regexp = "[a-z]*", message = "Имя адаптера должно быть таким-то")
    public String adapterName;

    @NotNull
    public String adapterDescription;

    @NotNull
    public String adapterShFileName;

    @NotNull
    public String errorLogFile;

    public TemplatePropertyDto() {}

}
