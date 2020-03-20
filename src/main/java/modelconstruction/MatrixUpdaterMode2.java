package modelconstruction;

import TCFGmodel.TCFG;
import TCFGmodel.TCFGUtil;
import com.alibaba.fastjson.JSONObject;
import faultdiagnosis.Anomaly;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.*;

import static humanfeedback.SuspiciousRegionMonitor.*;


public class MatrixUpdaterMode2 implements MatrixUpdater {

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
            long slidingWindowStep = parameterTool.getLong("slidingWindowStep");

            //Initialize paramMatrix and counter
            TransferParamMatrix tempTransferParamMatrix = transferParamMatrix.value();
            if (tempTransferParamMatrix == null) {
                tempTransferParamMatrix = new TransferParamMatrix();
                transferParamMatrix.update(tempTransferParamMatrix);
            }
            TCFGUtil.counter counter = counterValueState.value();
            if (counter == null) {
                counter = new TCFGUtil().new counter();
            }
            //Update transferParamMatrix in share memory
            if (counter.modResult(parameterTool.getInt("writeInterval")) == 0) {
                try {
                    //handle human feedback
                    while (!tuningRegion.isEmpty()) {
                        Anomaly anomaly = tuningRegion.pollAnomalyFromQueue();
                        if (anomaly.getAnomalyType() == "Latency") {
                            
                        }
                        if (anomaly.getAnomalyType() == "Redundancy") {

                        }
                    }
                    //update share memory
                    int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
                    int tcfgSize = parameterTool.getInt("TCFGSize");
                    String tempTransferParamMatrixStr = JSONObject.toJSONString(tempTransferParamMatrix);
                    TCFG.sm.write(tcfgSize, transferParamMatrixSize, tempTransferParamMatrixStr.getBytes("UTF-8"));
                } catch (Exception e) {
                    System.out.println("serialization failure");
                }
            }
            counterValueState.update(counter);

            int trainingFlag = parameterTool.getInt("trainingFlag");

            //TCFG Construction process
            List<String> priorEventIDList = tempTransferParamMatrix.getEventIDList();
            List<Tuple7> tempList = new ArrayList<>();
            Iterator<Tuple7<String, String, String, String, String, String, String>> iter = input.iterator();

            while (iter.hasNext()) {
                Tuple7 in = iter.next();
                //add new template into the matrix during training
                if (trainingFlag == 1 && !priorEventIDList.contains(in.f6)) {
                    tempTransferParamMatrix.addNewTemplate((String)in.f6,(String)in.f4,parameterTool.getDouble("alpha"));
                }
                long inTime = Long.parseLong((String)in.f0);
                //add new template to matrix update process during training
                if (trainingFlag == 1) {
                    tempList.add(in);
                }
                tempList = TCFGUtil.deleteReplica(tempList);
                tempList = new IndenpendencyFilter().filterIndependentNodes(tempList,tempTransferParamMatrix,slidingWindowStep,parameterTool.getLong("delta"));
                if (inTime-context.window().getStart() > slidingWindowStep) {
                    MatrixUpdaterMode2 TCFGConstructer = new MatrixUpdaterMode2();
                    List<Tuple7> slidingWindowList = TCFGConstructer.getTimeWindowLogList(inTime-slidingWindowStep,tempList);
                    //Start grad computation
                    for (Tuple7 tuple: slidingWindowList) {
                        if (slidingWindowList.indexOf(tuple) == slidingWindowList.size()-1) {
                            break;
                        }
                        //update the time matrix during training
                        if (trainingFlag == 1) {
                            tempTransferParamMatrix.updateTimeMatrix((String)in.f6,(String)tuple.f6,inTime- Long.parseLong((String)tuple.f0));
                        }
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
                }
            }
            //Start matrix update
            tempTransferParamMatrix.updateParamMatrix(parameterTool.getDouble("gamma"));
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

        @Override
        public void close() throws Exception {
            super.close();
        }
    }
}
