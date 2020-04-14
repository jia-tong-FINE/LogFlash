package workflow;


import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WorkFlowMode1 implements WorkFlow{
    @Override
    public void workflow(StreamExecutionEnvironment env, DataStreamSource<String> dataStream, ParameterTool parameter) throws Exception{

    }
}
