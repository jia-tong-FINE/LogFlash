package faultdiagnosis;

import java.util.ArrayList;
import java.util.List;

public class WhiteList {

    List<Anomaly> anomalyWhiteList;

    WhiteList() {
        anomalyWhiteList = new ArrayList<>();
    }

    public void addWhiteList(Anomaly anomaly) {
        anomalyWhiteList.add(anomaly);
    }
}
