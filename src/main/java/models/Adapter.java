package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Adapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    public String title;

    public String jarFileName;

    public Date postedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    public List<Long> templateIds;

    //@ElementCollection(fetch = FetchType.EAGER)
    //public List<AdapterEvent> adapterEventList;

    @Column(length = 5000) //init with VARCHAR(1000)
    public String content;

    /*
    @ElementCollection(fetch=FetchType.EAGER)
    public List<Long> authorIds;
*/
    private String name;
    private String jarFilePath;
    private String checkStatusCommands;
    private String startCommands;
    private String stopCommands;
    private String status;
    private String logFile;
    private String errorLogFile;
    private String heartbeatStatus;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "adapter_id")
    @JsonIgnore
    private List<AdapterEvent> adapterEvents;

    public Adapter() {
        this.logFile = "";
        this.adapterEvents = null;
    }

    public Adapter(String title, String content, String jarFileName, Long templateId) {
        this.templateIds = Lists.newArrayList(templateId);
        this.title = title;
        this.content = content;
        this.jarFileName = jarFileName;
        this.postedAt = new Date();
        this.status = "Unknown";
        this.startCommands = "";
        this.stopCommands = "";
        this.jarFilePath = "";
        this.logFile = "";
        this.errorLogFile = "";
        this.adapterEvents = null;
    }

    public Adapter(String title, String content, Long templateId) {
        this.templateIds = Lists.newArrayList(templateId);
        this.title = title;
        this.content = content;
        this.jarFileName = "";
        this.postedAt = new Date();
        this.status = "Unknown";
        this.startCommands = "";
        this.stopCommands = "";
        this.jarFilePath = "";
        this.logFile = "";
        this.errorLogFile = "";
        this.adapterEvents = null;
    }

    public String getJarFilePath() {
        return jarFilePath;
    }

    public void setJarFilePath(String jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    public String getCheckStatusCommands() {
        return checkStatusCommands;
    }

    public void setCheckStatusCommands(String checkStatusCommands) {
        this.checkStatusCommands = checkStatusCommands;
    }

    public String getStartCommands() {
        return startCommands;
    }

    public void setStartCommands(String startCommands) {
        this.startCommands = startCommands;
    }

    public String getStopCommands() {
        return stopCommands;
    }

    public void setStopCommands(String stopCommands) {
        this.stopCommands = stopCommands;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getErrorLogFile() {
        return errorLogFile;
    }

    public void setErrorLogFile(String errorLogFile) {
        this.errorLogFile = errorLogFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeartbeatStatus() {
        return heartbeatStatus;
    }

    public void setHeartbeatStatus(String heartbeatStatus) {
        this.heartbeatStatus = heartbeatStatus;
    }

    public List<AdapterEvent> getAdapterEvents() {
        return adapterEvents;
    }

    public void setAdapterEvents(List<AdapterEvent> adapterEvents) {
        this.adapterEvents = adapterEvents;
    }
}