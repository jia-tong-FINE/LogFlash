package humanfeedback;

import faultdiagnosis.Anomaly;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TuningRegion {

    Queue<Anomaly> anomalyQueue = new LinkedList<>();
    List<String> eventWhiteList = new ArrayList<>();

    public void addAnomalyToQueue(Anomaly e) {
        anomalyQueue.offer(e);
    }

    public void pollAnomalyFromQueue() {
        anomalyQueue.poll();
    }

    public Anomaly showAnomalyFromQueue() {
        return anomalyQueue.element();
    }

    public void addEventToWhiteList(String eventID) {
        eventWhiteList.add(eventID);
    }

}
