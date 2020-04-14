package workflow;


import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface WorkFlow {
    void workflow(StreamExecutionEnvironment env, DataStreamSource<String> dataStream, ParameterTool parameter) throws Exception;
}
