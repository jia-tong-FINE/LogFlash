package templatemining;

import java.io.Serializable;
import java.util.List;

class LogCluster implements Serializable {
    public List<String> logTemplate;

    public LogCluster() {
    }

    LogCluster(List<String> logTemplate) {
        this.logTemplate = logTemplate;
    }

    List<String> getLogTemplate() {
        return logTemplate;
    }

    void setLogTemplate(List<String> logTemplate) {
        this.logTemplate = logTemplate;
    }

}
