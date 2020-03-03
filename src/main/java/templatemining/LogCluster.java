package templatemining;

import java.io.Serializable;
import java.util.List;

class LogCluster implements Serializable {
    private List<String> logTemplate;
    private List<Integer> logIDL;

    LogCluster(List<String> logTemplate, List<Integer> logIDL) {
        this.logTemplate = logTemplate;
        this.logIDL = logIDL;
    }

    LogCluster(List<String> logTemplate) {
        this.logTemplate = logTemplate;
    }


    List<String> getLogTemplate() {
        return logTemplate;
    }

    List<Integer> getLogIDL() {
        return logIDL;
    }

    void setLogTemplate(List<String> logTemplate) {
        this.logTemplate = logTemplate;
    }

    void setLogIDL(int logID) {
        this.logIDL.add(logID);
    }
}
