package humanfeedback;

import faultdiagnosis.Anomaly;
import java.util.LinkedList;
import java.util.Queue;

public class SuspiciousRegion {

    Queue<Anomaly> anomalyQueue = new LinkedList<>();

    public void addAnomalyToQueue(Anomaly e) {
        anomalyQueue.offer(e);
    }

    public void pollAnomalyFromQueue() {
        anomalyQueue.poll();
    }

    public Anomaly showAnomalyFromQueue() {
        return anomalyQueue.element();
    }


}
