//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
//import org.apache.flink.util.Collector;
//
//import java.util.Properties;
//
//public class EntranceTest {
//    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        Properties properties = new Properties();
//        properties.setProperty("bootstrap.servers", "localhost:9092");
//
//        properties.setProperty("zookeeper.connect", "localhost:2181");
//        properties.setProperty("group.id", "test");
//
//        FlinkKafkaConsumer<String> wordConsumer = new FlinkKafkaConsumer<>("word", new SimpleStringSchema(), properties);
//
//        wordConsumer.setStartFromEarliest();
//
//        DataStream<String> txt = env
//                .addSource(wordConsumer);
//
//        DataStream<WordWithCount> windowCount = txt.flatMap(new FlatMapFunction<String, WordWithCount>() {
//            public void flatMap(String value, Collector<WordWithCount> out) throws Exception {
//                String[] splits = value.split("\\s");
//                for (String word : splits) {
//                    out.collect(new WordWithCount(word, 1L));
//                }
//            }
//        })
//                .keyBy("word")
//                .timeWindow(Time.seconds(2), Time.seconds(1))
//                .sum("count");
//
//        windowCount.print();
//        env.execute("streaming word count");
//
//    }
//
//    public static class WordWithCount {
//        public String word;
//        public long count;
//
//        public WordWithCount() {
//        }
//
//        public WordWithCount(String word, long count) {
//            this.word = word;
//            this.count = count;
//        }
//
//        @Override
//        public String toString() {
//            return "WordWithCount{" +
//                    "word='" + word + '\'' +
//                    ", count=" + count +
//                    '}';
//        }
//    }
//}
//
