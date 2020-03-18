package humanfeedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackAwareMatrix {
    private List<String> eventIDList;
    private Map<String, String> eventIDandContent;
    private Map<String, Map<String, Double>> paramMatrix;
    private Map<String, Map<String, Double>> gradMatrix;
    private Map<String, Map<String, Long>> timeMatrix;

    FeedbackAwareMatrix() {
        eventIDList = new ArrayList<>();
        eventIDandContent = new HashMap<>();
        paramMatrix = new HashMap<>();
        gradMatrix = new HashMap<>();
        timeMatrix = new HashMap<>();
    }
}
