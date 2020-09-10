package templatemining;

import java.io.Serializable;
import java.util.List;

class LogCluster implements Serializable {
    public List<String> logTemplate;
    public String eventID;

    public LogCluster() {
    }

    LogCluster(List<String> logTemplate, String eventID) {
        this.logTemplate = logTemplate;
        this.eventID = eventID;
    }

    List<String> getLogTemplate() {
        return logTemplate;
    }

    void setLogTemplate(List<String> logTemplate) {
        this.logTemplate = logTemplate;
    }

    String getEventID() {
        return eventID;
    }

    void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
