package humanfeedback;

import faultdiagnosis.Anomaly;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FeedBackFalseAlarms {

    private Queue<Anomaly> falseAlarms = new LinkedBlockingQueue<>();

    public Anomaly getAnomalyFromFalseAlarms() {
        return falseAlarms.poll();
    }

    public void addAnomalyToFalseAlarms(Anomaly anomaly) {
        falseAlarms.offer(anomaly);
    }

    public boolean isEmply() {
        return falseAlarms.isEmpty();
    }


}
