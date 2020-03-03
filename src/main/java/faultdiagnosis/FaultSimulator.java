package faultdiagnosis;

import Dao.MysqlUtil;
import TemplateMining.LogParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//simulate three anomalies in hadoop or ADC?? check the performance??
public class FaultSimulator {
    String log_file_path;
    double probability;

    FaultSimulator(String log_file_path, double probability) {
        this.log_file_path = log_file_path;
        this.probability = probability;
    }

    private String sequenceAnomalyInjection(String logLine) {
        String newLogLine = "";
        return newLogLine;
    }

    private String latencyAnomalyInjection(String logLine, int latency) throws Exception {
        String newLogLine = "";
        String[] tokens = logLine.split(" ");
        String time = tokens[3];
        LogParser lp = new LogParser();
        String stamp = lp.TimetoStamp(time,"HH:mm:ss,SSS");
        int newStamp = Integer.valueOf(stamp) + latency;
        String newTime = lp.StamptoTime(String.valueOf(newStamp), "HH:mm:ss,SSS");
        for (int i=0;i<tokens.length;i++) {
            if (i == 3) {
                newLogLine = newLogLine + newTime + " ";
            }
            else {
                newLogLine = newLogLine + tokens[i] + " ";
            }
        }
        newLogLine = newLogLine.trim();
        return newLogLine;
    }

    private String redundancyAnomalyInjection(String logLine) {
        String newLogLine = "";
        return newLogLine;
    }


    public static void main(String args[]) throws Exception {
        //simulator: test the performance of fault diagnosis approach
        FaultSimulator fs = new FaultSimulator("E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_logs",0.01);
        LogParser parser = new LogParser();
        String inputDir = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\for_log";
        File file = new File(inputDir);
        File[] tempList = file.listFiles();
        List<String> files = new ArrayList();
        List<String> failureLogFilePath = new ArrayList<>();
        List<String> normalLogFilePath = new ArrayList<>();
        MysqlUtil mysqlUtil = new MysqlUtil();


        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
//                        System.out.println(tempList[i].getAbsolutePath());
                String faultType = mysqlUtil.getFailureTypeByFaultId(tempList[i].getName());
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
        int all_failures = 0;
        List<String> failure_details = new ArrayList<>();
        String injected_dir = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_logs";
        String injected_detail_dir = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_details";
        for (String filePath: normalLogFilePath) {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String logLine = "";
            List<String> newLogList = new ArrayList<>();
            Queue<String> faultLogQueue = new LinkedList<>();
            while((logLine = br.readLine()) != null ) {
                //latency problem injection
//                if (logLine.startsWith("@")) {
//                    double rand = Math.random();
//                    if (rand<fs.probability) {
//                        all_failures ++;
//                        String failure_detail = filePath + "\t" + logLine;
//                        failure_details.add(failure_detail);
//                        faultLogQueue.offer(fs.latencyAnomalyInjection(logLine,1000));
//                    }
//                    else {
//                        String logStamp = parser.TimetoStamp(logLine.split(" ")[3],"HH:mm:ss,SSS");
//                        while(!faultLogQueue.isEmpty()){
//                            String faultLogLine = faultLogQueue.element();
//                            String faultLogStamp = parser.TimetoStamp(faultLogLine.split(" ")[3],"HH:mm:ss,SSS");
//                            if (Integer.valueOf(logStamp) > Integer.valueOf(faultLogStamp)) {
//                                newLogList.add(faultLogQueue.poll());
//                            }else{
//                                break;
//                            }
//                        }
//                        newLogList.add(logLine);
//                    }
//                }
                //latency problem injection
                if (logLine.startsWith("@")) {
                    double rand = Math.random();
                    if (rand<fs.probability) {
                        all_failures ++;
                        String failure_detail = filePath + "\t" + logLine;
                        failure_details.add(failure_detail);
                    }
                    else {
                        newLogList.add(logLine);
                    }
                }
            }
            FileWriter fw = new FileWriter(injected_dir + "\\sequence_0.01\\" + filePath.split("\\\\")[5]);
            String logFileContent = "";
            for (String log: newLogList) {
                logFileContent = logFileContent + log + "\n";
            }
            fw.write(logFileContent.trim());
            fw.flush();
            fw.close();
        }
        FileWriter fw_fault = new FileWriter(injected_detail_dir + "\\sequence_0.01");
        String failure_detail_content = "";
        for (String failure_detail: failure_details) {
            failure_detail_content = failure_detail_content + all_failures + "\t" + failure_detail + "\n";
        }
        fw_fault.write(failure_detail_content.trim());
        fw_fault.flush();
        fw_fault.close();
    }
}
