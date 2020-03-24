package modelconstruction;

import java.io.Serializable;
import java.util.*;


//Transfer Parameter Matrix
public class TransferParamMatrix implements Serializable {

    private List<String> eventIDList;
    private Map<String, String> eventIDandContent;
    private Map<String, Map<String, Double>> paramMatrix;
    private Map<String, Map<String, Double>> gradMatrix;
    private Map<String, Map<String, Long>> timeMatrix;

    public TransferParamMatrix() {
        eventIDList = new ArrayList<>();
        eventIDandContent = new HashMap<>();
        paramMatrix = new HashMap<>();
        gradMatrix = new HashMap<>();
        timeMatrix = new HashMap<>();
    }

    //Update the parameter matrix with gradient matrix
    //gamma is the update step length
    public void updateParamMatrix(double gamma){
        for (String alphai : gradMatrix.keySet()) {
            Map<String, Double> tempMap = paramMatrix.get(alphai);
            for (String alphaj : gradMatrix.get(alphai).keySet()) {
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                Double tempParam = paramMatrix.get(alphai).get(alphaj);
                Double tempGrad = gradMatrix.get(alphai).get(alphaj);
                double result = tempParam.doubleValue() - gamma * tempGrad.doubleValue();
                if (result < 0) {
                    result = 0;
                }
                tempMap.put(alphaj, Double.valueOf(result));
            }
            paramMatrix.put(alphai,tempMap);
        }
    }

    public void updateTimeMatrix(String eventi, String eventj, long timeWeight) {
        if (timeMatrix.get(eventj).get(eventi) >= timeWeight) {
            return;
        }
        Map<String, Long> tempTimeColumn = timeMatrix.get(eventj);
        tempTimeColumn.put(eventi, Long.valueOf(timeWeight));
        timeMatrix.put(eventj,tempTimeColumn);
    }

    //check the consistency of paramMatrix and gradMatrix
    public boolean checkConsistency() {
        boolean flag = true;
        try {
            if (paramMatrix.size() != gradMatrix.size()) {
                flag = false;
            }
        }catch (Exception e) {
        }
        return flag;
    }

    public void updateGradMatrix(String eventi, String eventj, double gradient) {
        if (gradient == 0) {
            return;
        }
        if ((gradMatrix.get(eventj).get(eventi).doubleValue() != 0) && (gradient <= gradMatrix.get(eventj).get(eventi).doubleValue())) {
            return;
        }
        double newGradient = gradient;
        Map<String, Double> tempGradColumn = gradMatrix.get(eventj);
        tempGradColumn.put(eventi, Double.valueOf(newGradient));
        gradMatrix.put(eventj,tempGradColumn);
    }

    public void clearGradMatrix() {
        for (String alphai : gradMatrix.keySet()) {
            Map<String, Double> tempMap = gradMatrix.get(alphai);
            for (String alphaj : gradMatrix.get(alphai).keySet()) {
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                tempMap.put(alphaj, Double.valueOf(0));
            }
            gradMatrix.put(alphai,tempMap);
        }
    }

    //add new template into the paramMatrix and gradMatrix
    public void addNewTemplate(String EventID, String EventContent, Double alpha) {
        if (eventIDList.contains(EventID)) return;
        eventIDList.add(EventID);
        eventIDandContent.put(EventID,EventContent);
        Map<String, Double> newParamColumn = new HashMap<>();
        Map<String, Double> newGradColumn = new HashMap<>();
        Map<String, Long> newTimeColumn = new HashMap<>();
        for (String key: paramMatrix.keySet()) {
            newParamColumn.put(key,alpha);
            newGradColumn.put(key, Double.valueOf(0));
            newTimeColumn.put(key, Long.valueOf(0));

            Map<String, Double> tempParamColumn = paramMatrix.get(key);
            tempParamColumn.put(EventID,alpha);
            paramMatrix.put(key,tempParamColumn);

            Map<String, Double> tempGradColumn = gradMatrix.get(key);
            tempGradColumn.put(EventID, Double.valueOf(0));
            gradMatrix.put(key,tempGradColumn);

            Map<String, Long> tempTimeColumn = timeMatrix.get(key);
            tempTimeColumn.put(EventID, Long.valueOf(0));
            timeMatrix.put(key,tempTimeColumn);
        }
        newParamColumn.put(EventID,alpha);
        newGradColumn.put(EventID, Double.valueOf(0));
        newTimeColumn.put(EventID, Long.valueOf(0));
        paramMatrix.put(EventID,newParamColumn);
        gradMatrix.put(EventID,newGradColumn);
        timeMatrix.put(EventID,newTimeColumn);
    }

    //delete expired template
    public void deleteExpiredTemplate(String EventID) {
        if (!eventIDList.contains(EventID)) return;
        Iterator<String> eventIDIt = eventIDList.iterator();
        while (eventIDIt.hasNext()) {
            if (eventIDIt.next() == EventID) {
                eventIDIt.remove();
            }
        }
        eventIDandContent.remove(EventID);
        paramMatrix.remove(EventID);
        gradMatrix.remove(EventID);
        timeMatrix.remove(EventID);

        for (String inNode: paramMatrix.keySet()) {
            paramMatrix.get(inNode).remove(EventID);
            gradMatrix.get(inNode).remove(EventID);
            timeMatrix.get(inNode).remove(EventID);
        }

    }

    public void decay(double beta) {
        for (String alphai : paramMatrix.keySet()) {
            Map<String, Double> tempMap = paramMatrix.get(alphai);
            for (String alphaj : paramMatrix.get(alphai).keySet()) {
                if (paramMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() != 0.0) {
                    continue;
                }
                Double tempParam = paramMatrix.get(alphai).get(alphaj);
                double result = tempParam * beta;
                tempMap.put(alphaj, Double.valueOf(result));
            }
            paramMatrix.put(alphai, tempMap);
        }
    }


    public List<String> getEventIDList() {
        return eventIDList;
    }

    public void setEventIDList(List<String> eventIDList) {
        this.eventIDList = eventIDList;
    }

    public Map<String, Map<String, Double>> getParamMatrix() {
        return paramMatrix;
    }

    public void setParamMatrix(Map<String, Map<String, Double>> paramMatrix) {
        this.paramMatrix = paramMatrix;
    }

    public Map<String, Map<String, Double>> getGradMatrix() {
        return gradMatrix;
    }

    public void setGradMatrix(Map<String, Map<String, Double>> gradMatrix) {
        this.gradMatrix = gradMatrix;
    }

    public Map<String, String> getEventIDandContent() {
        return eventIDandContent;
    }

    public void setEventIDandContent(Map<String, String> eventIDandContent) {
        this.eventIDandContent = eventIDandContent;
    }

    public Map<String, Map<String, Long>> getTimeMatrix() {
        return timeMatrix;
    }

    public double getNorm(){
        Map<String, Map<String, Double>> matrix = getParamMatrix();
        double t = 0;
        for (Map.Entry<String, Map<String, Double>> entry : matrix.entrySet()) {
            for (double value : entry.getValue().values())
                t += Math.pow(value, 2);
        }
        return Math.sqrt(t);
    }
}
