package humanfeedback;

import faultdiagnosis.Anomaly;
import java.util.LinkedList;
import java.util.Queue;

public class SuspiciousRegion {

    public Queue<Anomaly> sequenceAnomalyQueue = new LinkedList<>();
    public Queue<Anomaly> latencyAnomalyQueue = new LinkedList<>();
    public Queue<Anomaly> redundancyAnomalyQueue = new LinkedList<>();

    public boolean isEmpty() {
        if (sequenceAnomalyQueue.isEmpty() && latencyAnomalyQueue.isEmpty() && redundancyAnomalyQueue.isEmpty()) {
            return true;
        }
        return false;
    }

}
