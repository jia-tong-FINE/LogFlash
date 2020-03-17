package faultdiagnosis;

import TCFGmodel.ShareMemory;
import TCFGmodel.TCFG;
import TCFGmodel.TCFGUtil;
import modelconstruction.TransferParamMatrix;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;

//Fault Diagnosis with Sequence anomaly, Redundancy anomaly and latency anomaly
public class FaultDiagnosisMode2 implements FaultDiagnosis{

    public Anomaly faultDiagnosisProcess (TCFG tcfg, List<Tuple7> tempList) {
        Tuple7 latestNode = tempList.get(tempList.size()-1);
        if (latestNode.f6.equals("f80a2e40")||latestNode.f6.equals("4e81c689")|| latestNode.f6.equals("3a294bba") || latestNode.f6.equals("c1be6b3b") || latestNode.f6.equals("f254962d") || latestNode.f6.equals("4e0d8acb") || latestNode.f6.equals("8b232782") || latestNode.f6.equals("172d727c") ||latestNode.f6.equals("4fe6a4f8") || latestNode.f6.equals("36fbaa86") || latestNode.f6.equals("5e9cb693") || latestNode.f6.equals("f5ffd670") || latestNode.f6.equals("3872f636") || latestNode.f6.equals("1ffd3268")) {
            return null;
        }

        List<String> tempListId = new ArrayList<>();
        for (Tuple7 node: tempList) {
            tempListId.add((String)node.f6);
        }
        tempListId.remove(tempListId.size()-1);
        //Redundancy Anomaly
        boolean redundancy_flag = true;
        for (int i=0; i< tcfg.getNodes().size(); i++) {
            if (latestNode.f6.equals(tcfg.getNodes().get(i).getNode_id())) {
                redundancy_flag = false;
                break;
            }
        }
        if (redundancy_flag) {
            Anomaly anomaly = new Anomaly((String) latestNode.f6,latestNode,tempList,tempList,"Redundancy");
            return anomaly;
        }
        //Sequence Anomaly
        boolean is_latency = false;
        boolean end_node_flag = false;
        if (tempListId.size() < 2) {
            return null;
        }
        for (int i=0; i< tcfg.getEdges().size(); i++) {
            String inNodeId = tcfg.getEdges().get(i).getIn_node().getNode_id();
            String outNodeId = tcfg.getEdges().get(i).getOut_node().getNode_id();
            long timeWeight = tcfg.getEdges().get(i).getTime_weight();
//            if (inNodeId.equals("a66a46c7") || outNodeId.equals("a66a46c7")) {
//                System.out.println(inNodeId + " " +outNodeId);
//            }
            if (outNodeId.equals(latestNode.f6)) {
                if (tempListId.contains(inNodeId)) {
                    if ((Long.parseLong((String)latestNode.f0)- Long.parseLong((String)tempList.get(tempListId.indexOf(inNodeId)).f0)) <= timeWeight) {
                        return null;
                    }else {
                        is_latency = true;
                    }
                }
            }
        }

        if (is_latency == true) {
            List<Tuple7> tempList_l = new ArrayList<>();
            for (Tuple7 tuple: tempList) {
                tempList_l.add(tuple);
            }
            Anomaly anomaly = new Anomaly((String) latestNode.f6,latestNode,tempList,detectSuspiciousRequest(tcfg,tempList_l),"Latency");
            return anomaly;
        }
        end_node_flag = true;

        if (end_node_flag) {
            List<Tuple7> tempList_l = new ArrayList<>();
            for (Tuple7 tuple: tempList) {
                tempList_l.add(tuple);
            }
            List<Tuple7> suspiciousRequest = detectSuspiciousRequest(tcfg,tempList_l);
            suspiciousRequest.add(tempList.get(tempList.size()-1));
            Anomaly anomaly = new Anomaly((String) latestNode.f6,latestNode,tempList,suspiciousRequest,"Sequence");
            return anomaly;
        }
        return null;
    }

    public Anomaly interruptionFaultDiagnosisProcess (TCFG tcfg, Tuple7 lastNode) {
        for (int i = 0; i < tcfg.getEdges().size(); i++) {
            String inNodeId = tcfg.getEdges().get(i).getIn_node().getNode_id();
            if (inNodeId.equals(lastNode.f6)) {
                Anomaly anomaly = new Anomaly((String) lastNode.f6,lastNode,null,null,"Interruption");
                return anomaly;
            }
        }
        return null;
    }

    @Override
    public List<Tuple7> detectSuspiciousRequest(TCFG tcfg, List<Tuple7> tempList) {
        List<Tuple7> suspiciousRequest = new ArrayList<>();
        while (tempList.size() != 0) {
            boolean find_flag = false;
            for (int i=0; i< tcfg.getEdges().size(); i++) {
                String inNodeId = tcfg.getEdges().get(i).getIn_node().getNode_id();
                String outNodeId = tcfg.getEdges().get(i).getOut_node().getNode_id();
                if (tempList.get(tempList.size()-1).f6.equals(outNodeId)) {
                    for (int j = tempList.size()-2; j >= 0; j--) {
                        if (tempList.get(j).f6.equals(inNodeId)) {
                            if (!suspiciousRequest.contains(tempList.get(tempList.size()-1))) {
                                suspiciousRequest.add(tempList.get(tempList.size()-1));
                            }
                            if (!suspiciousRequest.contains(tempList.get(j))) {
                                suspiciousRequest.add(tempList.get(j));
                            }

                            for (int k = tempList.size()-1; k >= 0; k--) {
                                if (k == j) {
                                    break;
                                }
                                tempList.remove(k);
                                find_flag = true;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            if (!find_flag) {
                tempList.remove(tempList.size() - 1);
            }
        }
        List<Tuple7> suspiciousRequestReverse = new ArrayList<>();
        for (int i = suspiciousRequest.size()-1 ; i >= 0 ; i --) {
            suspiciousRequestReverse.add(suspiciousRequest.get(i));
        }
        return suspiciousRequestReverse;
    }

    @Override
    public double calProbability(double ti, double tj, double alphaji, long timeWindow, long delta) {
        return 0;
    }

    @Override
    public double calProbabilityOfCurrentEntry(List<Tuple7> logList, Map<String, Map<String, Double>> paramMatrix, long timeWindow, long delta) {
        return 0;
    }

    public static class FaultDiagnosisProcess extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        private ValueState<TCFG> tcfgValueState;
        private ValueState<TCFGUtil.counter> counterValueState;


        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> elements, Collector<String> out) throws Exception {
            ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            long slidingWindowStep = parameterTool.getLong("slidingWindowStep");
            TCFG tempTcfgValueState = tcfgValueState.value();

            //Initialize paramMatrix and counter
            if (tempTcfgValueState == null) {
                tempTcfgValueState = new TCFG();
                try {
                    int tcfgSize = parameterTool.getInt("TCFGSize");
                    byte[] b = new byte[tcfgSize];
                    TCFG.sm.read(0, tcfgSize, b);
                    JSON.parse(new String(b,"UTF-8"));
                    tcfgValueState.update(tempTcfgValueState);
                } catch (Exception e) {
                    System.out.println("serialization failure");
                }
            }
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil.counter();
            }
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<TCFG> descriptor1 =
                    new ValueStateDescriptor<>(
                            "tcfgValueState", // the state name
                            TCFG.class // type information
                    );
            tcfgValueState = getRuntimeContext().getState(descriptor1);

            ValueStateDescriptor<TCFGUtil.counter> descriptor2 =
                    new ValueStateDescriptor<>(
                            "counterValueState", // the state name
                            TCFGUtil.counter.class // type information
                    );
            counterValueState = getRuntimeContext().getState(descriptor2);

            super.open(parameters);
        }

        @Override
        public void close() throws Exception {
            super.close();
        }
    }
}
