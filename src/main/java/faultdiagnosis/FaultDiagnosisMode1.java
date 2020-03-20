package faultdiagnosis;

import TCFGmodel.TCFG;
import TCFGmodel.TCFGUtil;
import org.apache.flink.api.java.tuple.Tuple7;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Fault diagnosis with probability inference
public class FaultDiagnosisMode1 implements FaultDiagnosis {

    //Power Law
    @Override
    public double calProbability(double ti, double tj, double alphaji,long timeWindow, long delta) {

        double prob = (alphaji/delta)* Math.pow(((TCFGUtil.minMax((double)(ti-tj),delta,timeWindow))*delta+delta)/(double)delta,-1-alphaji);
        return prob;

    }

    @Override
    public double calProbabilityOfCurrentEntry(List<Tuple7> logList, Map<String, Map<String, Double>> paramMatrix, long timeWindow, long delta) {

        TCFGUtil faultDiagnosisUtil = new TCFGUtil();
        Tuple7 logEntryi = logList.get(logList.size()-1);
        List infectedProbList = new ArrayList();
        List suvivalProbList = new ArrayList();
        for (int i = 0; i < logList.size(); i++) {
            Tuple7 logEntryj = logList.get(i);
            if (!paramMatrix.keySet().contains(logEntryi.f6) || !paramMatrix.keySet().contains(logEntryj.f6)) {
                infectedProbList.add(0.0);
                suvivalProbList.add(1.0);
            }
            else if (Long.parseLong((String) logEntryi.f0)- Long.parseLong((String) logEntryj.f0) < delta) {
                logEntryj.f0 = String.valueOf(Long.parseLong((String)logEntryi.f0) - delta -1);
            }
            else {
                Double alphaji = paramMatrix.get(logEntryj.f6).get(logEntryi.f6);
                if (Long.parseLong((String)logEntryi.f0) - Long.parseLong((String)logEntryj.f0) <= delta) {
                    logEntryj.f0 = String.valueOf(Long.parseLong((String)logEntryi.f0) - delta -1);
                }
                double infectedProb = calProbability(Long.parseLong((String) logEntryi.f0), Long.parseLong((String) logEntryj.f0), alphaji,timeWindow, delta);
                infectedProbList.add(infectedProb);
                double survivalProb = 1-faultDiagnosisUtil.calDefinitIntegral(delta, TCFGUtil.minMax(Long.parseLong((String) logEntryi.f0)- Long.parseLong((String) logEntryj.f0),delta,timeWindow)*delta + delta, 100, alphaji, delta);
                suvivalProbList.add(survivalProb);
            }
        }

        double probSum = 0;
//        List<String> existingTemplates = new ArrayList<>();

        for (int j = infectedProbList.size()-1; j >=0; j--) {
            if ((double)infectedProbList.get(j) > 0.1) {
                probSum = 0.5;
                break;
            }

            //Tuple7 logEntryj = logList.get(j);
//            if (existingTemplates.contains(logEntryj.f6)) {
//                continue;
//            }
//            existingTemplates.add((String)logEntryj.f6);
            double survivalAll = 1;
            List<String> existingTemplates_in = new ArrayList<>();
            for (int x = infectedProbList.size()-1; x >= 0; x--) {
                if (j == x) {
                    continue;
                }
                //Tuple7 logEntryx = logList.get(x);
//                if (existingTemplates_in.contains(logEntryx.f6)) {
//                    continue;
//                }
//                existingTemplates_in.add((String)logEntryx.f6);
                survivalAll = survivalAll * (double)suvivalProbList.get(x);
                //System.out.println(survivalAll);
            }
            double probAll = (double)infectedProbList.get(j) * survivalAll;

            probSum = probSum + probAll;

        }

//        if (probSum < 0.01) {
////        List<String> existingTemplates = new ArrayList<>();
//
//            System.out.println("infectedProbList: " + infectedProbList);
//            System.out.println("suvivalProbList: " + suvivalProbList);
//            System.out.println("prob: " + probSum);
//        }
        return probSum;
    }

    @Override
    public List<Tuple7> detectSuspiciousRequest(TCFG tcfg, List<Tuple7> logList) {
        return null;
    }

    @Override
    public Anomaly faultDiagnosisProcess(TCFG tcfg, List<Tuple7> tempList) {
        return null;
    }
}
