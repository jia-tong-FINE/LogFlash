package humanfeedback;

import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class SuspiciousRegionMonitor {

    public static class SuspiciousRegionMonitoring extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        //Tuple7 = <time, level, component, content, eventTemplate, parameterList, eventID>
        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> input, Collector<String> out) throws Exception {
            Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();
            Tuple7 in = iter.next();
            long inTime = Long.parseLong((String) in.f0);
        }
    }
}
