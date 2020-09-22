import TCFGmodel.TCFGUtil;
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
import java.util.ArrayList;
import java.util.List;
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
            case "files":
                //String dir = "E:/中兴项目/实验/上电阶段数据_191219/hjm/release 5G AAU/67214_1571794768181";
                String dir = "E:/中兴项目/实验/tecs_log_good/tecs_log";

                List filelist = new ArrayList();
                List<File> newfilelist = getFileList(filelist,dir);
                for (File file: newfilelist) {

                    DataStreamSource<String> dataStream = env.readTextFile(file.getAbsolutePath());
                    System.out.println(file.getAbsolutePath());
                    try {
                        WorkFlow workFlow2 = new WorkFlowMode2();
                        workFlow2.workflow(env, dataStream, parameter);
                    }catch (Exception e){
                        System.out.println("error");
                    }
                }
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
//        env.getConfig().setGlobalJobParameters(parameter);
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

    public static List<File> getFileList(List filelist,String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(filelist,files[i].getAbsolutePath()); // 获取文件绝对路径
                //} else if (fileName.startsWith("swm_vmp-")) {
                } else if (fileName.startsWith("nova-compute")) {
                    filelist.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return filelist;
    }
}

