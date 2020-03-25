import TCFGmodel.ShareMemory;
import TCFGmodel.TCFG;
import TCFGmodel.TCFGConstructor;
import TCFGmodel.TCFGUtil;
import faultdiagnosis.FaultDiagnosisMode2;
import humanfeedback.FeedbackListener;
import humanfeedback.SuspiciousRegionMonitor;
import modelconstruction.MatrixUpdaterMode2;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.log4j.Logger;
import templatemining.Parse;
import workflow.WatermarkGenerator;

import java.io.File;
import java.util.Properties;

public class Entrance {

    public static void workFlow1(StreamExecutionEnvironment env, DataStreamSource<String> dataStream, ParameterTool parameter) throws Exception {
        dataStream.map(line -> Tuple2.of(parameter.get("logData"), line))
                .returns(Types.TUPLE(Types.STRING, Types.STRING))
                .keyBy(t -> t.f0)
                .process(new Parse())
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("timeWindow"))))
                //.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .process(new MatrixUpdaterMode2.TransferParamMatrixUpdate());
        //.print();
        //.writeAsCsv(output_dir + File.separator + logName + "_flink.csv", FileSystem.WriteMode.OVERWRITE);
        //addsink
        env.execute();
    }

    public static void workFlow2(StreamExecutionEnvironment env, DataStreamSource<String> dataStream, ParameterTool parameter) throws Exception {
        TCFGUtil tcfgUtil = new TCFGUtil();
        tcfgUtil.initiateShareMemory();
        DataStream<Tuple7<String, String, String, String, String, String, String>> templateStream = dataStream.map(line -> Tuple2.of(parameter.get("logData"), line))
                .returns(Types.TUPLE(Types.STRING, Types.STRING))
                .keyBy(t -> t.f0)
                .process(new Parse());
        //ParamMatrix Update
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new MatrixUpdaterMode2.TransferParamMatrixUpdate());
        //Fault Diagnosis
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new FaultDiagnosisMode2.FaultDiagnosisProcess());
        //TCFG Construction
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new TCFGConstructor.TCFGConstructionProcess());
        //Human Feedback-aware Tuning
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new SuspiciousRegionMonitor.SuspiciousRegionMonitoring());
        env.execute();
    }

    public static DataStreamSource<String> getDataStream(StreamExecutionEnvironment env, Logger log, ParameterTool parameter) throws Exception {
        String source = parameter.get("sourceName");
        switch (source) {
            default:
                log.error("Source type can be file, rabbitmq or kafka.");
                break;
            case "file":
                String input_dir = String.format("src/main/resources/%s/raw", parameter.get("logData"));
                return env.readTextFile(input_dir + File.separator + parameter.get("logName"));
            case "rabbitmq":
                RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                        .setHost(parameter.get("rabbitmqHost"))
                        .setPort(Integer.parseInt(parameter.get("rabbitmqPort")))
                        .setVirtualHost(parameter.get("virtualHost"))
                        .setUserName(parameter.get("rabbitmqUser"))
                        .setPassword(parameter.get("rabbitmqPassword"))
                        .build();
                return env.addSource(new RMQSource<String>(connectionConfig, parameter.get("queue"), true, new SimpleStringSchema()));
            case "kafka":
                Properties properties = new Properties();
                properties.setProperty("bootstrap.servers", parameter.get("bootstrapServer"));
                properties.setProperty("zookeeper.connect", parameter.get("zookeeperConnect"));
                properties.setProperty("group.id", parameter.get("groupID"));
                FlinkKafkaConsumer<String> wordConsumer = new FlinkKafkaConsumer<>(parameter.get("topic"), new SimpleStringSchema(), properties);
                wordConsumer.setStartFromEarliest();
                return env.addSource(wordConsumer);
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        final Logger log = Logger.getLogger(Entrance.class);
        ParameterTool parameter = ParameterTool.fromPropertiesFile("src/main/resources/config.properties");
        String sp = parameter.get("shareMemoryFilePath");
        TCFG.sm = new ShareMemory(sp, "TCFG");

        FeedbackListener listener = new FeedbackListener();
        listener.start();

        String mode = parameter.get("workFlowMode");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.getConfig().setGlobalJobParameters(parameter);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<String> dataStream = getDataStream(env, log, parameter);
        switch (mode) {
            default:
                log.error("workFlowMode can only be 1 or 2");
                break;
            case "1":
                workFlow1(env, dataStream, parameter);
                break;
            case "2":
                workFlow2(env, dataStream, parameter);
        }
    }
//
//        // Kafka connection below.
//
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//
//        properties.setProperty("zookeeper.connect", "localhost:2181");
//        properties.setProperty("group.id", "test");
//
//        FlinkKafkaConsumer<String> wordConsumer = new FlinkKafkaConsumer<>("word", new SimpleStringSchema(), properties);
//
//        wordConsumer.setStartFromEarliest();
//
//        DataStream<String> txt = env
//                .addSource(wordConsumer);
//
//        // RabbitMQ connection below.
//        // /*
//        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
//                .setHost("localhost")
//                .setPort(5672)
//                .setVirtualHost("/")
//                .setUserName("guest")
//                .setPassword("guest")
//                .build();
//        DataStream<String> txt = env
//                .addSource(new RMQSource<String>(
//                        connectionConfig,
//                        "word",
//                        true,
//                        new SimpleStringSchema()))
//                .setParallelism(1);
//        // */
//        DataStream<WordWithCount> windowCount = txt.flatMap(new FlatMapFunction<String, WordWithCount>() {
//            public void flatMap(String value, Collector<WordWithCount> out) throws Exception {
//                String[] splits = value.split("\\s");
//                for (String word : splits) {
//                    out.collect(new WordWithCount(word, 1L));
//                }
//            }
//        })
//                .keyBy("word")
//                .timeWindow(Time.seconds(2), Time.seconds(1))
//                .sum("count");
//
//        windowCount.print();
//        env.execute("streaming word count");
//
//    }
//
//    public static class WordWithCount {
//        public String word;
//        public long count;
//
//        public WordWithCount() {
//        }
//
//        public WordWithCount(String word, long count) {
//            this.word = word;
//            this.count = count;
//        }
//
//        @Override
//        public String toString() {
//            return "WordWithCount{" +
//                    "word='" + word + '\'' +
//                    ", count=" + count +
//                    '}';
//        }
//    }
}

