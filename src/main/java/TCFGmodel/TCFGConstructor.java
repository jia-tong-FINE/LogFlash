package TCFGmodel;

import com.alibaba.fastjson.JSONObject;
import modelconstruction.TransferParamMatrix;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import workflow.Config;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import static humanfeedback.SuspiciousRegionMonitor.tuningRegion;

public class TCFGConstructor {

    public static class TCFGConstructionProcess extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        private ValueState<TCFGUtil.counter> counterValueState;

        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> input, Collector<String> out) throws Exception {
            ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil().new counter();
                counterValueState.update(counter);
            }

            if (counter.modResult(parameterTool.getInt("TCFGWriteInterval")) == 0) {
                //updat TCFG
                TCFGUtil tcfgUtil = new TCFGUtil();
                TransferParamMatrix transferParamMatrix = tcfgUtil.getMatrixFromMemory();
                TCFG tcfg = new TCFG();
                tcfg.paramMatrix2TCFG(transferParamMatrix, parameterTool.getLong("delta"));
                //handle human feedback(Lack of testing)
                List<String> whiteList = tuningRegion.getEventWhiteList();
                List<TCFG.Edge> edges = tcfg.getEdges();
                for (String whiteEventID : whiteList) {
                    for (int i = 0; i < edges.size(); i++) {
                        if (edges.get(i).out_node.node_id == whiteEventID) {
                            edges.remove(i--);
                        }
                    }
                }
                tcfg.setEdges(edges);
                tcfgUtil.saveTCFGInMemory(tcfg);
                counterValueState.update(counter);

                //store in database
                List list = new ArrayList<>();
                list.add(transferParamMatrix.getEventInfo());
                list.add(transferParamMatrix.getParamMatrix());
                String paramMatrixJSON = JSONObject.toJSONString(list);
                File file = new File("E:/中兴项目/实验/paramMatrix.json");
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(file,false);
                fileWritter.write(paramMatrixJSON
                );
//                MysqlUtil mysqlUtil = new MysqlUtil();
//                mysqlUtil.updateTCFG(paramMatrixJSON);
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
