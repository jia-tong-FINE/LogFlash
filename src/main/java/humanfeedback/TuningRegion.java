package humanfeedback;

import TCFGmodel.TCFGUtil;
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

    public Anomaly pollAnomalyFromQueue() {
        return anomalyQueue.poll();
    }

    public Anomaly showAnomalyFromQueue() {
        return anomalyQueue.element();
    }

    public void addEventToWhiteList(String eventID) {
        eventWhiteList.add(eventID);
    }

    public void deleteEventFromWhiteList(String eventID) {
        eventWhiteList.remove(eventID);
    }

    public boolean isEmpty() {
        return anomalyQueue.isEmpty();
    }

    public List<String> getEventWhiteList() {
        return eventWhiteList;
    }

}
