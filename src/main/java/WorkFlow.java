import TCFGmodel.ShareMemory;
import TCFGmodel.TCFG;
import dao.MysqlUtil;
import modelconstruction.TCFGConstructerMode1;
import modelconstruction.TCFGConstructerMode2;
import modelconstruction.TCFGConstructerMode3;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.streaming.api.datastream.DataStream;
import templatemining.FlinkDrain;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class WorkFlow {

    public static void main(String[] args) throws Exception {

        final Logger log = Logger.getLogger(WorkFlow.class);
        ParameterTool parameter = ParameterTool.fromPropertiesFile("src/main/resources/config.properties");
        String sp = parameter.get("shareMemoryFilePath");
        TCFG.sm = new ShareMemory(sp,"TCFG");
        switch (parameter.get("workFlowMode")) {
            default:
                log.error("workFlowMode can only be 1 or 2");
                break;
            case "1":
                String logdata = "adc";
                String logName = "adc-06-04-2019-2";
                //String logName = "yarn-resourcemanager-cleaned";
                String input_dir = String.format("src/main/resources/data/%s/raw", logdata);

                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                env.getConfig().setGlobalJobParameters(parameter);
                env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                DataStreamSource<String> dataStream = env.readTextFile(input_dir + File.separator + logName);

                dataStream.map(line -> Tuple2.of(logdata, line))
                        .returns(Types.TUPLE(Types.STRING, Types.STRING))
                        .keyBy(t -> t.f0)
                        .process(new FlinkDrain.Parse())
                        .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                        .keyBy(t -> t.f2)
                        .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("timeWindow"))))
                        //.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .process(new TCFGConstructerMode1.TransferParamMatrixUpdate());
                //.print();
                //.writeAsCsv(output_dir + File.separator + logName + "_flink.csv", FileSystem.WriteMode.OVERWRITE);
                //addsink
                env.execute();
                break;
            case "2":

                String logdata2 = "adc";
                String logName2 = "adc-06-04-2019-2";
                String input_dir2 = String.format("src/main/resources/data/%s/raw", logdata2);
                StreamExecutionEnvironment env2 = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                env2.getConfig().setGlobalJobParameters(parameter);
                env2.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                DataStreamSource<String> dataStream2 = env2.readTextFile(input_dir2 + File.separator + logName2);

                DataStream<Tuple7<String,String,String,String,String,String,String>> templateStream= dataStream2.map(line -> Tuple2.of(logdata2, line))
                        .returns(Types.TUPLE(Types.STRING, Types.STRING))
                        .keyBy(t -> t.f0)
                        .process(new FlinkDrain.Parse());
                templateStream
                        .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                        .keyBy(t -> t.f2)
                        .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))),Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                        .process(new TCFGConstructerMode2.TransferParamMatrixUpdate());

                env2.execute();
                break;

        }
    }
}
