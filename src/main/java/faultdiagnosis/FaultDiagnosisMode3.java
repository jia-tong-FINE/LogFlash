package faultdiagnosis;

import TCFGmodel.TCFG;
import org.apache.flink.api.java.tuple.Tuple7;
import java.util.List;
import java.util.Map;

public class FaultDiagnosisMode3 implements FaultDiagnosis{


    @Override
    public double calProbability(double ti, double tj, double alphaji, long timeWindow, long delta) {
        return 0;
    }

    @Override
    public double calProbabilityOfCurrentEntry(List<Tuple7> logList, Map<String, Map<String, Double>> paramMatrix, long timeWindow, long delta) {
        return 0;
    }

    @Override
    public List<Tuple7> detectSuspiciousRequest(TCFG tcfg, List<Tuple7> logList) {
        return null;
    }

    @Override
    public Anomaly faultDiagnosisProcess(TCFG tcfg, List<Tuple7> tempList) {
        return null;
    }
}
