package faultdiagnosis;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaultSimulationAnalysis {

    public static void main(String args[]) throws Exception {

        String diagnosis_result_file_path = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_details\\sequence_0.01_result";
        String injected_log_file_path = "E:\\在线自更新故障诊断模型与工具\\实验数据\\for_log\\injected_details\\sequence_0.01";

        FileReader diagnosis_result_reader = new FileReader(diagnosis_result_file_path);
        BufferedReader diagnosis_result_breader = new BufferedReader(diagnosis_result_reader);
        FileReader injected_log_reader = new FileReader(injected_log_file_path);
        BufferedReader injected_log_breader = new BufferedReader(injected_log_reader);

        String diagnosis_result_line = "";
        Map<String, List<String>> failure_map = new HashMap<>();
        while ((diagnosis_result_line = diagnosis_result_breader.readLine()) != null) {
            String[] elements = diagnosis_result_line.split("\t");
            List<String> failure_log_list = new ArrayList<>();
            String fileName = elements[0].split("\\\\")[6];
            for (int i = 1; i < elements.length; i++) {
//                System.out.println(elements[i]);
                failure_log_list.add(elements[i]);
            }
            failure_map.put(fileName,failure_log_list);
        }
        System.out.println(failure_map);
        String injected_log_line = "";
        Map<String, List<String>> injected_map = new HashMap<>();
        while ((injected_log_line = injected_log_breader.readLine()) != null) {
            String[] elements = injected_log_line.split("\t");
            List<String> injected_log_list = new ArrayList<>();
            String fileName = elements[1].split("\\\\")[5];

            if (injected_map.keySet().contains(fileName)) {
                List<String> tempList = injected_map.get(fileName);
                tempList.add(elements[2]);
                injected_map.put(fileName,tempList);
            }else {
                injected_log_list.add(elements[2]);
                injected_map.put(fileName, injected_log_list);
            }
        }
        System.out.println(injected_map);

        int right_records = 0;
        int all_records = 0;
        //test the recall rate
        for(String key: injected_map.keySet()) {
            all_records = all_records + injected_map.get(key).size();
            if (failure_map.get(key).size() >= injected_map.get(key).size()) {
                right_records = right_records + injected_map.get(key).size();
            }else {
                right_records = right_records + failure_map.get(key).size();
            }
        }
        System.out.println((double)right_records/all_records);
    }
}
