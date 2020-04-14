import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.log4j.Logger;
import workflow.WorkFlow;
import workflow.WorkFlowMode1;
import workflow.WorkFlowMode2;

import java.io.File;
import java.util.Properties;

public class Entrance {

    public static DataStreamSource<String> getDataStream(StreamExecutionEnvironment env, Logger log, ParameterTool parameter){
        String source = parameter.get("sourceName");
        switch (source) {
            default:
                log.error("Source type can be file, rabbitmq, socket or kafka.");
                break;
            case "socket":
                return env.socketTextStream(parameter.get("socketHost"), Integer.parseInt(parameter.get("socketPort")), "\n");
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
                WorkFlow workFlow1 = new WorkFlowMode1();
                workFlow1.workflow(env, dataStream, parameter);
                break;
            case "2":
                WorkFlow workFlow2 = new WorkFlowMode2();
                workFlow2.workflow(env, dataStream, parameter);
        }
    }

}

