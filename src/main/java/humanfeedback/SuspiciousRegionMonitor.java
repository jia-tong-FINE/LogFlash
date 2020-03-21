package humanfeedback;

import TCFGmodel.TCFG;
import TCFGmodel.TCFGUtil;
import faultdiagnosis.*;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;



import java.util.Iterator;

public class SuspiciousRegionMonitor {
    public static FeedBackFalseAlarms feedBackFalseAlarms = new FeedBackFalseAlarms();
    public static SuspiciousRegion suspiciousRegion = new SuspiciousRegion();
    public static TuningRegion tuningRegion = new TuningRegion();
    public SuspiciousRegionMonitor() {
        TCFGUtil tcfgUtil = new TCFGUtil();
        tcfgUtil.getTuningRegionFromMemory();
    }

    public static class SuspiciousRegionMonitoring extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        private ValueState<TCFGUtil.counter> counterValueState;

        //Tuple7 = <time, level, component, content, eventTemplate, parameterList, eventID>
        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> input, Collector<String> out) throws Exception {
            ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil().new counter();
                counterValueState.update(counter);
            }

            if (counter.modResult(parameterTool.getInt("falseAlarmsProcessingInterval")) == 0) {
                //Processing human feedback false alarms
                while (!feedBackFalseAlarms.isEmply()) {
                    Anomaly anomaly = feedBackFalseAlarms.getAnomalyFromFalseAlarms();
                    if (anomaly.getAnomalyType() == "Sequence") {
                        suspiciousRegion.sequenceAnomalyQueue.offer(anomaly);
                    }
                    if (anomaly.getAnomalyType() == "Latency") {
                        tuningRegion.addAnomalyToQueue(anomaly);
                    }
                    if (anomaly.getAnomalyType() == "Redundancy") {
                        tuningRegion.addAnomalyToQueue(anomaly);
                    }
                }

                //Processing anomaly which is in Suspicious State
                if (!suspiciousRegion.isEmpty()) {
                    FaultDiagnosis faultDiagnosis = new FaultDiagnosisMode2();
                    TCFGUtil tcfgUtil = new TCFGUtil();
                    TCFG tcfg = tcfgUtil.getTCFGFromMemory();
                    long suspiciousTimeForSequenceAnomaly = parameterTool.getLong("suspiciousTimeForSequenceAnomaly");
                    long suspiciousTimeForLatencyAnomaly = parameterTool.getLong("suspiciousTimeForLatencyAnomaly");
                    long suspiciousTimeForRedundancyAnomaly = parameterTool.getLong("suspiciousTimeForRedundancyAnomaly");
                    Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();
                    Tuple7 in = iter.next();
                    long inTime = Long.parseLong((String) in.f0);
                    //handle sequence anomaly
                    while (suspiciousRegion.sequenceAnomalyQueue.size() > 0) {
                        Anomaly anomaly = suspiciousRegion.sequenceAnomalyQueue.element();
                        long anomalyTime = Long.parseLong((String) anomaly.getAnomalyLog().f0);
                        //beyond suspicious time window
                        if (inTime - anomalyTime > suspiciousTimeForSequenceAnomaly) {
                            Anomaly newAnomaly = faultDiagnosis.faultDiagnosisProcess(tcfg, anomaly.getAnomalyLogList());
                            if (newAnomaly == null || newAnomaly.getAnomalyType() != "Sequence") {
                                suspiciousRegion.sequenceAnomalyQueue.poll();
                            } else {
                                tuningRegion.addEventToWhiteList((String) anomaly.getAnomalyLog().f6);
                                suspiciousRegion.sequenceAnomalyQueue.poll();
                            }
                        } else {
                            break;
                        }
                    }
                    //handle latency anomaly
                    while (suspiciousRegion.latencyAnomalyQueue.size() > 0) {
                        Anomaly anomaly = suspiciousRegion.latencyAnomalyQueue.element();
                        long anomalyTime = Long.parseLong((String) anomaly.getAnomalyLog().f0);
                        //beyond suspicious time window
                        if (inTime - anomalyTime > suspiciousTimeForLatencyAnomaly) {
                            suspiciousRegion.latencyAnomalyQueue.poll();
                        } else {
                            break;
                        }
                    }
                    //handle redundancy anomaly
                    while (suspiciousRegion.redundancyAnomalyQueue.size() > 0) {
                        Anomaly anomaly = suspiciousRegion.redundancyAnomalyQueue.element();
                        long anomalyTime = Long.parseLong((String) anomaly.getAnomalyLog().f0);
                        //beyond suspicious time window
                        if (inTime - anomalyTime > suspiciousTimeForRedundancyAnomaly) {
                            suspiciousRegion.redundancyAnomalyQueue.poll();
                        } else {
                            break;
                        }
                    }
                }
                TCFGUtil tcfgUtil = new TCFGUtil();
                tcfgUtil.saveTuningRegionInMemory();
                counterValueState.update(counter);
            }

        }

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<TCFGUtil.counter> descriptor1 =
                    new ValueStateDescriptor<>(
                            "counterValueState", // the state name
                            TCFGUtil.counter.class // type information
                    );
            counterValueState = getRuntimeContext().getState(descriptor1);
            super.open(parameters);
        }

        @Override
        public void close() throws Exception {
            super.close();
        }
    }
}
