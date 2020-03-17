package faultdiagnosis;

import TCFGmodel.TCFG;
import org.apache.flink.api.java.tuple.Tuple7;

import java.util.List;
import java.util.Map;

public interface FaultDiagnosis {

    double calProbability(double ti, double tj, double alphaji, long timeWindow, long delta);

    double calProbabilityOfCurrentEntry(List<Tuple7> logList, Map<String, Map<String, Double>> paramMatrix, long timeWindow, long delta);

    List<Tuple7> detectSuspiciousRequest(TCFG tcfg, List<Tuple7> logList);
}
