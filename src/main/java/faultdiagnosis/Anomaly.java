package faultdiagnosis;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.flink.api.java.tuple.Tuple7;

import java.util.List;

public class Anomaly {

    @JSONField(name = "LogID")
    private String anomalyLogId;
    @JSONField(name = "Log")
    private Tuple7 anomalyLog;
    @JSONField(name = "LogList")
    private List<Tuple7> anomalyLogList;
    @JSONField(name = "Request")
    private List<Tuple7> suspectedAnomalyRequest;
    @JSONField(name = "Type")
    private String anomalyType;

    Anomaly() {
    }

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

    public void setAnomalyLogId(String anomalyLogId) {
        this.anomalyLogId = anomalyLogId;
    }

    public void setAnomalyLog(Tuple7 anomalyLog) {
        this.anomalyLog = anomalyLog;
    }

    public void setAnomalyLogList(List<Tuple7> anomalyLogList) {
        this.anomalyLogList = anomalyLogList;
    }

    public void setSuspectedAnomalyRequest(List<Tuple7> suspectedAnomalyRequest) {
        this.suspectedAnomalyRequest = suspectedAnomalyRequest;
    }

    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }
}
