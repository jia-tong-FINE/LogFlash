package modelconstruction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.io.Serializable;
import java.util.*;


//Transfer Parameter Matrix
public class TransferParamMatrix implements Serializable {

    private List<String> eventIDList;
    private Map<String, Tuple2<String,Long>> eventInfo;
    private Map<String, Map<String,MatrixTriple>> paramMatrix;

    public TransferParamMatrix() {
        eventIDList = new ArrayList<>();
        eventInfo = new HashMap<>();
        paramMatrix = new HashMap<>();
    }

    //Update the parameter matrix with gradient matrix
    //gamma is the update step length
    public void updateParam(double gamma, double alpha, String outNode, String inNode,  double gradient){
        if (!paramMatrix.keySet().contains(outNode)) {
            Map<String,MatrixTriple> matrixTriples = new HashMap<>();
            MatrixTriple triple = new MatrixTriple(alpha-gamma*gradient,0);
            matrixTriples.put(inNode,triple);
            paramMatrix.put(outNode,matrixTriples);
            return;
        }else if (!paramMatrix.get(outNode).keySet().contains(inNode)) {
            Map<String,MatrixTriple> matrixTriples = paramMatrix.get(outNode);
            MatrixTriple triple = new MatrixTriple(alpha-gamma*gradient,0);
            matrixTriples.put(inNode,triple);
            paramMatrix.put(outNode,matrixTriples);
            return;
        }
        Map<String,MatrixTriple> matrixTriples = paramMatrix.get(outNode);
        MatrixTriple triple = matrixTriples.get(inNode);
        MatrixTriple newTriple = new MatrixTriple(triple.getValue()-gamma*gradient,triple.getTimeWeight());
        if (alpha-gamma*gradient < 0) {
            triple.setValue(0);
        }
        matrixTriples.put(inNode,newTriple);
        paramMatrix.put(outNode,matrixTriples);
    }

    public void updateTimeWeight(String outNode, String inNode, long timeWeight) {

        if (!paramMatrix.containsKey(outNode)) return;
        else if (!paramMatrix.get(outNode).containsKey(inNode)) return;
        if (paramMatrix.get(outNode).get(inNode).getTimeWeight() >= timeWeight) {
            return;
        }
        Map<String,MatrixTriple> matrixTriples = paramMatrix.get(outNode);
        MatrixTriple triple = matrixTriples.get(inNode);
        MatrixTriple newTriple = new MatrixTriple(triple.getValue(),timeWeight);
        matrixTriples.put(inNode,newTriple);
        paramMatrix.put(outNode,matrixTriples);
    }


    public void clearParam() {
        paramMatrix.clear();
    }

    public void updateOccurTime(String eventID,String eventContent, Long eventTime) {
        Tuple2<String,Long> contentAndTime = new Tuple2<>(eventContent,eventTime);
        eventInfo.put(eventID,contentAndTime);
    }

    //add new template into the paramMatrix and gradMatrix
    public void addNewTemplate(String eventID, String eventContent, Long eventTime) {
        if (eventIDList.contains(eventID)) return;
        eventIDList.add(eventID);
        Tuple2<String,Long> contentAndTime = new Tuple2<>(eventContent,eventTime);
        eventInfo.put(eventID,contentAndTime);
    }

    public void deleteExpiredTemplate(String eventID) {
        eventIDList.remove(eventID);
        eventInfo.remove(eventID);
        paramMatrix.remove(eventID);
        for (String outNode: paramMatrix.keySet()) {
            for(String inNode: paramMatrix.get(outNode).keySet()){
                if (inNode.equals(eventID)) {
                    Map<String,MatrixTriple> newParamTriples = paramMatrix.get(outNode);
                    newParamTriples.remove(inNode);
                    paramMatrix.put(outNode,newParamTriples);
                    break;
                }
            }
        }
    }

    public void decay(double beta) {
        Map<String, Map<String,MatrixTriple>> newParamMatrix = new HashMap<>();
        for (String outNode: paramMatrix.keySet()) {
                Map<String,MatrixTriple> newParamTriples = new HashMap<>();
            for(String inNode: paramMatrix.get(outNode).keySet()){
                MatrixTriple newTriple = new MatrixTriple(paramMatrix.get(outNode).get(inNode).getValue()*beta,paramMatrix.get(outNode).get(inNode).getTimeWeight());
                newParamTriples.put(inNode,newTriple);
            }
            newParamMatrix.put(outNode,newParamTriples);
        }
        paramMatrix = newParamMatrix;
    }

    public double getParam(String outNode, String inNode) {
        if (!paramMatrix.keySet().contains(outNode)) {
            return 0;
        }else if (!paramMatrix.get(outNode).keySet().contains(inNode)) {
            return 0;
        }
        return paramMatrix.get(outNode).get(inNode).getValue();
    }

    public double getNorm(){
//        Map<String, Map<String, Double>> matrix = getParamMatrix();
//        double t = 0;
//        for (Map.Entry<String, Map<String, Double>> entry : matrix.entrySet()) {
//            for (double value : entry.getValue().values())
//                t += Math.pow(value, 2);
//        }
//        return Math.sqrt(t);
        return 0;
    }

    public List<String> getEventIDList() {
        return eventIDList;
    }

    public Map<String, Tuple2<String, Long>> getEventInfo() {
        return eventInfo;
    }

    public Map<String, Map<String, MatrixTriple>> getParamMatrix() {
        return paramMatrix;
    }
}
