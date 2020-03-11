package faultdiagnosis;

import TCFGmodel.TCFG;
import org.apache.flink.api.java.tuple.Tuple7;

import java.util.ArrayList;
import java.util.List;

public class FaultDiagnosisUnitMode3 {

    public Anomaly faultDiagnosisProcess (TCFG tcfg, List<Tuple7> tempList) {
        Tuple7 latestNode = tempList.get(tempList.size()-1);
        if (latestNode.f6.equals("c1be6b3b") || latestNode.f6.equals("f254962d") || latestNode.f6.equals("4e0d8acb") || latestNode.f6.equals("8b232782") || latestNode.f6.equals("172d727c") ||latestNode.f6.equals("4fe6a4f8") || latestNode.f6.equals("36fbaa86") || latestNode.f6.equals("5e9cb693") || latestNode.f6.equals("f5ffd670") || latestNode.f6.equals("3872f636") || latestNode.f6.equals("1ffd3268")) {
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
}
