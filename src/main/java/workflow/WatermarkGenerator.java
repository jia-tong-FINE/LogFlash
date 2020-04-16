package workflow;

import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class WatermarkGenerator {

    public static class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<Tuple7<String, String, String, String, String, String, String>> {

        private long currentMaxTimestamp;

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
            long maxOutOfOrderness = Long.parseLong(parameterTool.get("maxOutOfOrderness")); // timeWindow milliseconds
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple7<String, String, String, String, String, String, String> element, long previousElementTimestamp) {
            long timestamp = Long.parseLong(element.f0);
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    public static class TimeLagWatermarkGenerator implements AssignerWithPeriodicWatermarks<Tuple7<String, String, String, String, String, String, String>> {

        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        private final long maxTimeLag = Long.parseLong(parameterTool.get("timeSlag")); // timeSlag seconds

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(System.currentTimeMillis() - maxTimeLag);
        }

        @Override
        public long extractTimestamp(Tuple7<String, String, String, String, String, String, String> element, long previousElementTimestamp) {
            return Long.parseLong(element.f0);
        }
    }
}
