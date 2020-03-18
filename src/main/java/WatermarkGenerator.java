import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class WatermarkGenerator {

    public static class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<Tuple7<String, String, String, String, String, String, String>> {

        Properties properties = WatermarkGenerator.getConfig();

        private final long maxOutOfOrderness = Long.parseLong(properties.getProperty("maxOutOfOrderness")); // timeWindow milliseconds
        private long currentMaxTimestamp;

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
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

        Properties properties = WatermarkGenerator.getConfig();
        private final long maxTimeLag = Long.parseLong(properties.getProperty("timeSlag")); // timeSlag seconds

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

    public static Properties getConfig() {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/config.properties"));
            properties.load(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }
}
