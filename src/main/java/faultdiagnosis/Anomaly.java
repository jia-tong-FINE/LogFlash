package faultdiagnosis;

import org.apache.flink.api.java.tuple.Tuple7;

import java.util.List;

public class Anomaly {

    private String anomalyLogId;
    private Tuple7 anomalyLog;
    private List<Tuple7> anomalyLogList;
    private List<Tuple7> suspectedAnomalyRequest;
    private String anomalyType;

    Anomaly(String anomalyLogId, Tuple7 anomalyLog, List<Tuple7> anomalyLogList, List<Tuple7> suspectedAnomalyRequest, String anomalyType) {
        this.anomalyLogId = anomalyLogId;
        this.anomalyLog = anomalyLog;
        this.anomalyLogList = anomalyLogList;
        this.suspectedAnomalyRequest = suspectedAnomalyRequest;
        this.anomalyType = anomalyType;
    }

    public String getAnomalyLogId() {
        return anomalyLogId;
    }

    public Tuple7 getAnomalyLog() {
        return anomalyLog;
    }

    public List<Tuple7> getAnomalyLogList() {
        return anomalyLogList;
    }

    public List<Tuple7> getSuspectedAnomalyRequest() {
        return suspectedAnomalyRequest;
    }

    public String getAnomalyType() {
        return anomalyType;
    }
}
