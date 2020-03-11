import dao.MysqlUtil;
import modelconstruction.TCFGConstructerMode1;
import modelconstruction.TCFGConstructerMode2;
import modelconstruction.TCFGConstructerMode3;
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

    public static Properties getConfig() {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/config.properties"));
            properties.load(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) throws Exception {

        final Logger log = Logger.getLogger(WorkFlow.class);
        ParameterTool parameter = ParameterTool.fromPropertiesFile("src/main/resources/config.properties");
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
                        .flatMap(new FlinkDrain.Parse())
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
//                Plaint plaint2 = new Plaint();
//                plaint2.createGuiAndShow();
                String logdata2 = "adc";
                String logName2 = "adc-06-04-2019-2";
                String input_dir2 = String.format("src/main/resources/data/%s/raw", logdata2);
                //String output_dir2 = String.format("src/main/resources/data/%s/parsed", logdata2);
                StreamExecutionEnvironment env2 = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                env2.getConfig().setGlobalJobParameters(parameter);
                env2.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                DataStreamSource<String> dataStream2 = env2.readTextFile(input_dir2 + File.separator + logName2);

                dataStream2.map(line -> Tuple2.of(logdata2, line))
                        .returns(Types.TUPLE(Types.STRING, Types.STRING))
                        .keyBy(t -> t.f0)
                        .flatMap(new FlinkDrain.Parse())
                        .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                        .keyBy(t -> t.f2)
                        .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))),Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                        //.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .process(new TCFGConstructerMode2.TransferParamMatrixUpdate());
                //.print();
                //.writeAsCsv(output_dir + File.separator + logName + "_flink.csv", FileSystem.WriteMode.OVERWRITE);
                //addsink
                env2.execute();
                break;
            case "3":
                //Fault Diagnosis Experiment on Hadoop/Spark/Flink

                //Initialize correct result map
//                Map<String,String> correct_result = new HashMap<>();
//                correct_result.put("by","1002");
//                correct_result.put("hello","1002");
//                correct_result.put("is","1002");
//                correct_result.put("list","1002");
//                correct_result.put("my","1002");
//                correct_result.put("name","1002");
//                correct_result.put("please","1002");
//                correct_result.put("specify","1002");
//                correct_result.put("ssfi","1002");
//                correct_result.put("the","1002");
//                correct_result.put("word_dict","1002");
//                correct_result.put("yourself","1002");

                String inputDir = "E:\\在线自更新故障诊断模型与工具\\实验数据\\hadoop_amount0.5";
                File file = new File(inputDir);
                File[] tempList = file.listFiles();
                List<String> normalLogFilePath = new ArrayList<>();
                List<String> failureLogFilePath = new ArrayList<>();
                MysqlUtil mysqlUtil = new MysqlUtil();

                for (int i = 0; i < tempList.length; i++) {
                    if (tempList[i].isDirectory()) {
                        String faultType = mysqlUtil.getFailureTypeByFaultId(tempList[i].getName());
                        //judge failure flag
//                        if (new File(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log").exists()) {
//                            if (failureType.equals("0")) {
//                                normalLogFilePath.add(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log");
//                            }else {
//                                boolean failure_flag = false;
//                                Map<String,String> run_result = new HashMap<>();
//                                String runResult_filePath = tempList[i].getAbsolutePath() + "\\runResult.txt";
//                                FileReader fr = new FileReader(runResult_filePath);
//                                BufferedReader b_fr = new BufferedReader(fr);
//                                String result_line = "";
//                                while ((result_line = b_fr.readLine()) != null) {
//                                    if (result_line.length() > 1) {
//                                        String[] result_line_list = result_line.trim().split("\t");
//                                        run_result.put(result_line_list[0],result_line_list[1]);
//                                    }
//
//
//                                }
//                            }
//                            normalLogFilePath.add(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log");
//                        }
                        if (new File(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log").exists()) {
                            normalLogFilePath.add(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log");
                        }
                        //fault type
                        if (faultType.equals("Silent Early Exit") || faultType.equals("Detected Early Exit") || faultType.equals("Silent Data Corruption")) {
                            if (new File(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log").exists()) {
                                failureLogFilePath.add(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log");
                            }
                        }else if (faultType.equals("")) {
                            if (new File(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log").exists()) {
                                normalLogFilePath.add(tempList[i].getAbsolutePath() + "\\logs\\yarn--resourcemanager-hadoop-master.log");
                            }

                        }
                    }
                }
                for (String filePath: normalLogFilePath) {
                    System.out.println(filePath);
                    StreamExecutionEnvironment env3 = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                    env3.getConfig().setGlobalJobParameters(parameter);
                    env3.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                    DataStreamSource<String> dataStream3 = env3.readTextFile(filePath);
                    FileWriter writer = new FileWriter(parameter.get("anomalyResultFilePath"));
                    writer.write(filePath);
                    writer.close();

                    dataStream3.map(line -> Tuple2.of("1", line))
                            .returns(Types.TUPLE(Types.STRING, Types.STRING))
                            .keyBy(t -> t.f0)
                            .flatMap(new FlinkDrain.Parse())
                            .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                            .keyBy(t -> t.f2)
                            .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                            .process(new TCFGConstructerMode3.TransferParamMatrixUpdate());
                    env3.execute();
                    FileWriter writer1 = new FileWriter(parameter.get("anomalyResultFilePath"));
                    writer.write("\n");
                    writer.close();
                }
                break;

            case "4":
                String inputDir_adc = "E:\\在线自更新故障诊断模型与工具\\实验数据\\ADC_Log\\ADC_Log";
                File dir_adc = new File(inputDir_adc);
                File[] adc_list = dir_adc.listFiles();
                for (File adc_file: adc_list) {
                    //String output_dir2 = String.format("src/main/resources/data/%s/parsed", logdata2);
                    StreamExecutionEnvironment env4 = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                    env4.getConfig().setGlobalJobParameters(parameter);
                    env4.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                    DataStreamSource<String> dataStream4 = env4.readTextFile(adc_file.getAbsolutePath());

                    dataStream4.map(line -> Tuple2.of("adc_akk", line))
                            .returns(Types.TUPLE(Types.STRING, Types.STRING))
                            .keyBy(t -> t.f0)
                            .flatMap(new FlinkDrain.Parse())
                            .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                            .keyBy(t -> t.f2)
                            .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                            //.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                            .process(new TCFGConstructerMode2.TransferParamMatrixUpdate());
                    //.print();
                    //.writeAsCsv(output_dir + File.separator + logName + "_flink.csv", FileSystem.WriteMode.OVERWRITE);
                    //addsink
                    env4.execute();
                }
                break;
            case "5":
                //Simulation experiment on Hadoop
                String injected_dir = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_logs\\sequence_0.01";
                File injected_dir_file = new File(injected_dir);
                File[] injected_files = injected_dir_file.listFiles();

                for (File injected_file: injected_files) {

                    String injected_file_path = injected_file.getAbsolutePath();
                    FileWriter writer = new FileWriter("E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_details\\sequence_0.01_result", true);
                    writer.write("\n" + injected_file_path + "\t");
                    writer.close();
                    StreamExecutionEnvironment env5 = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
                    env5.getConfig().setGlobalJobParameters(parameter);
                    env5.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
                    DataStreamSource<String> dataStream5 = env5.readTextFile(injected_file_path);

                    dataStream5.map(line -> Tuple2.of("1", line))
                            .returns(Types.TUPLE(Types.STRING, Types.STRING))
                            .keyBy(t -> t.f0)
                            .flatMap(new FlinkDrain.Parse())
                            .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                            .keyBy(t -> t.f2)
                            .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                            //.window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                            .process(new TCFGConstructerMode3.TransferParamMatrixUpdate());
                    //.print();
                    //.writeAsCsv(output_dir + File.separator + logName + "_flink.csv", FileSystem.WriteMode.OVERWRITE);
                    //addsink
                    env5.execute();
                }
                //System.out.println(all_failures);
                break;
        }
    }
}
