package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Adapter {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    
    public String title;

    public String jarFileName;
    
    public Date postedAt;
    
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

    public Adapter() {
        this.logFile = "";
    }
    
    public Adapter(String title, String content, String jarFileName) {
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
    }

    public Adapter(String title, String content) {
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
}