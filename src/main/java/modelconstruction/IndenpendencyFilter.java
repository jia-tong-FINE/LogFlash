package modelconstruction;

import TCFGmodel.TCFGUtil;
import org.apache.flink.api.java.tuple.Tuple7;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndenpendencyFilter {

    private double calProbability(double ti, double tj, double alphaji,long timeWindow, long delta) {

        if (ti-tj <= delta) {
            tj = ti-delta-1;
        }
        double prob = (alphaji/delta)* Math.pow(((TCFGUtil.minMax((double)(ti-tj),delta,timeWindow))*delta+delta)/(double)delta,-1-alphaji);
        if (prob > 1) {
            prob = 1.0;
        }
        return prob;

    }

    public List<Tuple7> filterIndependentNodes(List<Tuple7> tempList, TransferParamMatrix transferParamMatrix, long timeWindow, long delta) {

        if (tempList.size()<2) {
            return tempList;
        }
        List<Tuple7> filteredLogList = new ArrayList<>();
        Tuple7 latestLog = tempList.get(tempList.size()-1);
        Tuple7 tempLog = tempList.get(tempList.size()-1);
        Map<String, Map<String, Double>> paramMatrix = transferParamMatrix.getParamMatrix();
        List<Tuple7> leftList = new ArrayList<>();
        for (int i = 0; i <= tempList.size()-2 ; i++) {
            leftList.add(tempList.get(i));
            filteredLogList.add(tempList.get(i));
        }
        filteredLogList.add(latestLog);
        double infection_prob = 1;
        while (leftList.size() != 0) {
            double max_transition_prob = 0;
            int max_log_id = leftList.size();
            for (int i = leftList.size()-1; i >=0 ; i--) {

                double alphaij = paramMatrix.get(leftList.get(i).f6).get(tempLog.f6);
                double prob_alphaij = calProbability(Double.valueOf((String)tempLog.f0), Double.valueOf((String)leftList.get(i).f0),alphaij,timeWindow,delta);

                if (max_transition_prob < prob_alphaij) {
                    max_transition_prob = prob_alphaij;
                    max_log_id = i;
                }
            }
            if (max_log_id == leftList.size()) {
                break;
            }

            infection_prob = infection_prob * max_transition_prob;
            double latest_alphaij = paramMatrix.get(leftList.get(max_log_id).f6).get(latestLog.f6);
            double latest_prob_alphaij = calProbability(Double.valueOf((String)latestLog.f0), Double.valueOf((String)leftList.get(max_log_id).f0),latest_alphaij,timeWindow,delta);
            if (infection_prob >= latest_prob_alphaij) {
                if (!tempLog.equals(latestLog)) {
                    filteredLogList.remove(filteredLogList.indexOf(leftList.get(max_log_id)));
                }
                tempLog = leftList.get(max_log_id);
                for (int i = leftList.size()-1; i >= 0; i --) {
                    leftList.remove(i);
                    if (i == max_log_id) {
                        break;
                    }
                }
            }
            else {
                break;
            }
        }
        return filteredLogList;
    }
}