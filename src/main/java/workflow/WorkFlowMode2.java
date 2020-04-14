package workflow;

import TCFGmodel.TCFGConstructor;
import faultdiagnosis.FaultDiagnosisMode2;
import humanfeedback.SuspiciousRegionMonitor;
import modelconstruction.MatrixUpdaterMode2;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import templatemining.Parse;


public class WorkFlowMode2 implements WorkFlow {

    @Override
    public void workflow(StreamExecutionEnvironment env, DataStreamSource<String> dataStream, ParameterTool parameter) throws Exception{
        Logger LOG = LoggerFactory.getLogger(WorkFlowMode2.class);
        DataStream<Tuple7<String, String, String, String, String, String, String>> templateStream = dataStream.map(line -> Tuple2.of(parameter.get("logData"), line))
                .returns(Types.TUPLE(Types.STRING, Types.STRING))
                .keyBy(t -> t.f0)
                .process(new Parse());
        //ParamMatrix Update
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new MatrixUpdaterMode2.TransferParamMatrixUpdate());
        //Human Feedback-aware Tuning
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new SuspiciousRegionMonitor.SuspiciousRegionMonitoring());
        //Fault Diagnosis
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new FaultDiagnosisMode2.FaultDiagnosisProcess());
        //TCFG Construction
        templateStream
                .assignTimestampsAndWatermarks(new WatermarkGenerator.BoundedOutOfOrdernessGenerator())
                .keyBy(t -> t.f2)
                .timeWindow(Time.milliseconds(Long.parseLong(parameter.get("slidingWindowSize"))), Time.milliseconds(Long.parseLong(parameter.get("slidingWindowStep"))))
                .process(new TCFGConstructor.TCFGConstructionProcess());
        JobExecutionResult result = env.execute();
        LOG.info(String.valueOf(result.getAllAccumulatorResults()));
    }
}
