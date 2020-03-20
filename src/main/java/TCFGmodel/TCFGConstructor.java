package TCFGmodel;

import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class TCFGConstructor {

    public static class TCFGConstructionProcess extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> elements, Collector<String> out) throws Exception {
            //Fault Diagosis Process defnition
            TCFG tcfg = new TCFG();
            //tcfg.paramMatrix2TCFG(tempTransferParamMatrix,parameterTool.getLong("delta"));
        }
    }
}
