package humanfeedback;

import com.sun.tools.javac.launcher.Main;
import faultdiagnosis.*;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;



import java.util.Iterator;

public class SuspiciousRegionMonitor {
    public static SuspiciousRegion suspiciousRegion = new SuspiciousRegion();
    public static TuningRegion tuningRegion = new TuningRegion();

    public static class SuspiciousRegionMonitoring extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        //Tuple7 = <time, level, component, content, eventTemplate, parameterList, eventID>
        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> input, Collector<String> out) throws Exception {
            if (!SuspiciousRegionMonitor.suspiciousRegion.isEmpty()) {
                FaultDiagnosis faultDiagnosis = new FaultDiagnosisMode2();
                ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
                long suspiciousTimeForSequenceAnomaly = parameterTool.getLong("suspiciousTimeForSequenceAnomaly");
                long suspiciousTimeForLatencyAnomaly = parameterTool.getLong("suspiciousTimeForLatencyAnomaly");
                long suspiciousTimeForRedundancyAnomaly = parameterTool.getLong("suspiciousTimeForRedundancyAnomaly");
                Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();
                Tuple7 in = iter.next();
                long inTime = Long.parseLong((String) in.f0);
                while (SuspiciousRegionMonitor.suspiciousRegion.sequenceAnomalyQueue.size() > 0) {
                    Anomaly anomaly = SuspiciousRegionMonitor.suspiciousRegion.sequenceAnomalyQueue.element();
                    long anomalyTime = Long.parseLong((String)anomaly.getAnomalyLog().f0);
                    //beyond suspicious time window
                    if (inTime-anomalyTime > suspiciousTimeForSequenceAnomaly) {
                        //faultDiagnosis.faultDiagnosisProcess(tcfg,anomaly.getAnomalyLogList());
                    }
                }
            }
        }
    }
}
