package modelconstruction;

import faultdiagnosis.Anomaly;
import faultdiagnosis.FaultDiagnosisUnitMode2;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class TCFGConstructerMode2 implements TCFGConstructer{
    public static Queue<Integer> anomalyQueue = new LinkedBlockingQueue();

    private List<Tuple7> getTimeWindowLogList(long startTime, List<Tuple7> logList) {
        List<Tuple7> timeWindowLogList = new ArrayList();
        for (Tuple7 tuple: logList) {
            if (Long.parseLong((String)tuple.f0)>startTime) {
                timeWindowLogList.add(tuple);
            }
        }
        return timeWindowLogList;
    }

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
        if (sum == 0) {
            return -1;
        }
        double subtrahend = 1/((TCFGUtil.minMax((double)(ti-tj),delta,timeWindow))*delta+delta)/sum;
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
//            long slidingWindowSize = parameterTool.getLong("slidingWindowSize");
            long slidingWindowStep = parameterTool.getLong("slidingWindowStep");
            TransferParamMatrix tempTransferParamMatrix = transferParamMatrix.value();

            //Initialize paramMatrix and counter
            if (tempTransferParamMatrix == null) {
                tempTransferParamMatrix = new TransferParamMatrix();
                File file = new File("src/main/resources/models/transferParamMatrix");
                if (file.exists()) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                        tempTransferParamMatrix = (TransferParamMatrix) in.readObject();
                        transferParamMatrix.update(tempTransferParamMatrix);
                    } catch (Exception e) {
                        System.out.println("serialization failure");
                    }
                }
            }
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil.counter();
            }

            //Fault Diagosis Process defnition
            TCFG tcfg = new TCFG();
            tcfg.paramMatrix2TCFG(tempTransferParamMatrix,parameterTool.getLong("delta"));
            FaultDiagnosisUnitMode2 faultDiagnosisUnit = new FaultDiagnosisUnitMode2();
//            int anomalies = 0;
            //TCFG Construction process
            List<String> priorEventIDList = tempTransferParamMatrix.getEventIDList();
            List<Tuple7> tempList = new ArrayList<>();
            Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();

            while (iter.hasNext()) {
                Tuple7 in = iter.next();
                // Be careful with the templates BOOM!
                if (!priorEventIDList.contains(in.f6)) {
                    tempTransferParamMatrix.addNewTemplate((String)in.f6,(String)in.f4,parameterTool.getDouble("alpha"));
                }
                long inTime = Long.parseLong((String)in.f0);
                tempList.add(in);
                tempList = TCFGUtil.deleteReplica(tempList);
                tempList = new IndenpendencyFilter().filterIndependentNodes(tempList,tempTransferParamMatrix,slidingWindowStep,parameterTool.getLong("delta"));
                if (inTime-context.window().getStart() > slidingWindowStep) {
                    TCFGConstructerMode2 TCFGConstructer = new TCFGConstructerMode2();
                    List<Tuple7> slidingWindowList = TCFGConstructer.getTimeWindowLogList(inTime-slidingWindowStep,tempList);

                    Anomaly anomaly = faultDiagnosisUnit.faultDiagnosisProcess(tcfg,slidingWindowList);
                    if (anomaly != null) {
                        //insert anomalies into database
//                        MysqlUtil mysqlUtil = new MysqlUtil();
//                        mysqlUtil.insertAnomaly(anomaly);
                        for (int i = 0; i< tcfg.getEdges().size(); i++) {
                            String inNode = tcfg.getEdges().get(i).getIn_node().node_id;
                            String outNode = tcfg.getEdges().get(i).getOut_node().node_id;
                            if (anomaly.getAnomalyLog().f6.equals(inNode)) {
                                System.out.println("inNode:" + inNode + " outNode: " + outNode);
                            }
                            if (anomaly.getAnomalyLog().f6.equals(outNode)) {
                                System.out.println("inNode:" + inNode + " outNode: " + outNode);
                            }
                        }
                        for (Tuple7 tuple:anomaly.getAnomalyLogList()) {
                            System.out.println(tuple.f6);
                        }
//                        FileWriter writer = new FileWriter("src/main/resources/models/anomalies", true);
//                        writer.write("1\n");
//                        writer.close();
                        System.out.println(anomaly.getAnomalyLog().f4 + " @ " + anomaly.getAnomalyLog().f6 + " @ " + anomaly.getAnomalyLog().f3);
                        System.out.println(anomaly.getAnomalyType());
                        List<String> anomalyList = new ArrayList<>();
                        for (Tuple7 log: anomaly.getAnomalyLogList()) {
                            anomalyList.add((String)log.f6);
                        }
//                        System.out.println(anomalyList);
//                        System.out.println(anomalies);
                    }
                    //Start grad computation
                    for (Tuple7 tuple: slidingWindowList) {
                        if (slidingWindowList.indexOf(tuple) == slidingWindowList.size()-1) {
                            break;
                        }
                        tempTransferParamMatrix.updateTimeMatrix((String)in.f6,(String)tuple.f6,inTime- Long.parseLong((String)tuple.f0));
                        double gradient = TCFGConstructer.calGradientForInfected(inTime, Long.parseLong((String)tuple.f0),tempTransferParamMatrix,slidingWindowList,slidingWindowStep,parameterTool.getLong("delta"));

                        if (gradient > parameterTool.getDouble("gradLimitation")) {
                            gradient = parameterTool.getDouble("gradLimitation");
                        }
                        if (gradient < -parameterTool.getDouble("gradLimitation")) {
                            gradient = -parameterTool.getDouble("gradLimitation");
                        }
                        if (gradient != 0) {
                            tempTransferParamMatrix.updateGradMatrix((String) in.f6, (String) tuple.f6, gradient);
                        }
                    }
                    //end grad computation
                }

            }
            //Start matrix update
            tempTransferParamMatrix.updateParamMatrix(parameterTool.getDouble("gamma"));
            tempTransferParamMatrix.decay(parameterTool.getDouble("beta"));
            tempTransferParamMatrix.clearGradMatrix();
            transferParamMatrix.update(tempTransferParamMatrix);
            //end matrix update
            if (counter.modResult() == 0) {
                tempTransferParamMatrix.saveParamMatrix("http://127.0.0.1:5000/api/data",null);
//                System.out.println(tempTransferParamMatrix.getParamMatrix());
            }
            counterValueState.update(counter);

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

        @Override
        public void close() throws Exception {

            //Model Serialization
            ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(
                    new File("src/main/resources/models/transferParamMatrix")));
            oo.writeObject(transferParamMatrix.value());
            System.out.println("model serialization success");
            //Model Serialization
            super.close();
        }
    }
}
