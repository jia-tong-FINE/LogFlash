package modelconstruction;

import TCFGmodel.TCFGUtil;
import faultdiagnosis.FaultDiagnosisMode1;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class MatrixUpdaterMode1 implements MatrixUpdater {
    public static Queue<Integer> anomalyQueue = new LinkedBlockingQueue();
    @Override
    public double calGradientForInfected(long ti, long tj, TransferParamMatrix transferParamMatrix, List<Tuple7> tempList, long timeWindow, long delta) {

        if (tempList.size() < 2) {
            return 0;
        }

        if (ti-tj <= delta) {
            tj = ti-delta-1;
        }

        double minuend = Math.log(((TCFGUtil.minMax((double)(ti-tj),delta,timeWindow))*delta+delta)/delta);
        double sum = 0;
        Tuple7 elementi = tempList.get(tempList.size()-1);
        for (int i=0; i < tempList.size()-1; i++) {

            Tuple7 elementk = tempList.get(i);
            if (Long.parseLong((String)elementi.f0) - Long.parseLong((String)elementk.f0) <= delta) {
                elementk.f0 = String.valueOf(Long.parseLong((String)elementi.f0) - delta -1);
            }

            Map<String, Map<String, Double>> paramMatrix = transferParamMatrix.getParamMatrix();

            double alphaki = paramMatrix.get(elementk.f6).get(elementi.f6).doubleValue();
            sum = sum + alphaki/((TCFGUtil.minMax(Long.parseLong((String)elementi.f0) - Long.parseLong((String)elementk.f0),delta,timeWindow))*delta+delta);
        }
//        if (elementi.f6.toString().equals("5e9cb693")) {
//            System.out.println("sum " + sum );
//            System.out.println("tj: " + tempList.get(tempList.size()-2).f6);
//            Map<String, Map<String,Double>> paramMatrix = transferParamMatrix.getParamMatrix();
//            System.out.println("tj: " + paramMatrix.get(tempList.get(tempList.size()-2).f6).get(elementi.f6).doubleValue());
//        }
        if (sum == 0) {
            return -1;
        }
        double subtrahend = 1/((TCFGUtil.minMax((double)(ti-tj),delta,timeWindow))*delta+delta)/sum;
//        if (true) {
//            for (int i=0; i < tempList.size()-1; i++) {
//
//                Tuple7 elementk = tempList.get(i);
//                if (Long.parseLong((String)elementi.f0) - Long.parseLong((String)elementk.f0) <= delta) {
//                    continue;
//                }
//
//                Map<String, Map<String,Double>> paramMatrix = transferParamMatrix.getParamMatrix();
//
//                double alphaki = paramMatrix.get(elementk.f6).get(elementi.f6).doubleValue();
//                System.out.println("alphaki: " + alphaki);
//                System.out.println("time interval: " + ((TCFGUtil.minMax(Long.parseLong((String)elementi.f0) - Long.parseLong((String)elementk.f0),timeWindow,delta))*delta+delta));
//            }
//            System.out.println("minuend" + minuend);
//            System.out.println("sum" + sum);
//            System.out.println("subtrahend" + subtrahend);
//            System.out.println(minuend-subtrahend);
//        }
        return minuend-subtrahend;
    }

    @Override
    public double calGradientForUninfected(long ti, long tj, TransferParamMatrix transferParamMatrix, List<Tuple7> tempList, long delta) {
        return 0;
    }

    public static class TransferParamMatrixUpdate extends ProcessWindowFunction<Tuple7<String, String, String, String, String, String, String>, String, String, TimeWindow> {

        private ValueState<TransferParamMatrix> transferParamMatrix;
        private ValueState<TCFGUtil.counter> counterValueState;

        @Override
        public void process(String s, Context context, Iterable<Tuple7<String, String, String, String, String, String, String>> input, Collector<String> out) throws Exception {

            ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            TransferParamMatrix tempTransferParamMatrix = transferParamMatrix.value();

            if (tempTransferParamMatrix == null) {
                tempTransferParamMatrix = new TransferParamMatrix();
            }
            //System.out.println(tempTransferParamMatrix.getParamMatrix());
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil.counter();
            }
            //Fault Diagosis Process
            FaultDiagnosisMode1 faultDiagnosisUnit = new FaultDiagnosisMode1();
            Iterator<Tuple7<String, String, String, String, String, String, String>> iter_f = input.iterator();
            List<Tuple7> tempList_f = new ArrayList<>();
            int anomalies = 0;
            while (iter_f.hasNext()) {
                Tuple7 in = iter_f.next();
                tempList_f.add(in);
                if (Long.parseLong((String) in.f0) - Long.parseLong((String)tempList_f.get(0).f0) < parameterTool.getLong("timeWindow")/2) {
                    continue;
                }

                double probability = faultDiagnosisUnit.calProbabilityOfCurrentEntry(tempList_f,tempTransferParamMatrix.getParamMatrix(),parameterTool.getLong("timeWindow"),parameterTool.getLong("delta"));
                if (probability <0.01) {
                    anomalies = anomalies + 1;
                    if (!tempList_f.get(tempList_f.size()-1).f6.toString().equals("4fe6a4f8")) {
                        for (int i = 0; i<tempList_f.size();i++) {
                            System.out.println(tempList_f.get(i).f0 + "?????" + tempList_f.get(i).f4 + ' ' + tempList_f.get(i).f6);
                        }
                        System.out.println("++++++++++++++++++++++++++++++");
                    }

                }
            }
            anomalyQueue.offer(anomalies);
            synchronized (anomalyQueue) {
                anomalyQueue.notify();
            }


            //TCFG Construction process
            List<String> priorEventIDList = tempTransferParamMatrix.getEventIDList();
            List<Tuple7> tempList = new ArrayList<>();
            Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();
            while (iter.hasNext()) {
                Tuple7 in = iter.next();
                tempList.add(in);
                // Be careful with the templates BOOM!
                if (!priorEventIDList.contains(in.f6)) {
                    tempTransferParamMatrix.addNewTemplate((String)in.f6,(String)in.f4,parameterTool.getDouble("alpha"));
                }
                tempList = TCFGUtil.deleteReplica(tempList);
                if (tempList.size() < 2) {
                    continue;
                }
                for (Tuple7 tuple: tempList) {
                    MatrixUpdaterMode1 TCFGConstructer = new MatrixUpdaterMode1();
                    double gradient = TCFGConstructer.calGradientForInfected(Long.parseLong((String)in.f0), Long.parseLong((String)tuple.f0),tempTransferParamMatrix,tempList,parameterTool.getLong("timeWindow"),parameterTool.getLong("delta"));
                    if (gradient > parameterTool.getDouble("gradLimitation")) {
                        gradient = parameterTool.getDouble("gradLimitation");
                    }
                    if (gradient < -parameterTool.getDouble("gradLimitation")) {
                        gradient = -parameterTool.getDouble("gradLimitation");
                    }
                    if (gradient != 0) {
                        tempTransferParamMatrix.updateGradMatrix((String) in.f6, (String) tuple.f6, gradient);
//                        System.out.println("templates: " + in.f6 + " " + tuple.f6);
//                        System.out.println("gradient: " + gradient);
                    }
                }
            }
            tempTransferParamMatrix.updateParamMatrix(parameterTool.getDouble("gamma"));
            //System.out.println(tempTransferParamMatrix.getGradMatrix());
            tempTransferParamMatrix.decay(parameterTool.getDouble("beta"));
            tempTransferParamMatrix.clearGradMatrix();
            transferParamMatrix.update(tempTransferParamMatrix);


        }

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<TransferParamMatrix> descriptor =
                    new ValueStateDescriptor<>(
                            "transferParamMatrix", // the state name
                            TransferParamMatrix.class // type information
                    );
            transferParamMatrix = getRuntimeContext().getState(descriptor);

            ValueStateDescriptor<TCFGUtil.counter> descriptor1 =
                    new ValueStateDescriptor<>(
                            "counterValueState", // the state name
                            TCFGUtil.counter.class // type information
                    );
            counterValueState = getRuntimeContext().getState(descriptor1);
            super.open(parameters);

        }
    }
}
