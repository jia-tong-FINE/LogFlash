package templatemining;

import joinery.DataFrame;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import TCFGmodel.TCFGUtil;

public class Parse extends KeyedProcessFunction<String, Tuple2<String, String>, Tuple7<String, String, String, String, String, String, String>> {
    private ValueState<Node> parseTree;
    private IntCounter templateNum = new IntCounter();
    private Logger log = LoggerFactory.getLogger(Parse.class);

    @Override
    public void processElement(Tuple2<String, String> input,
                               Context ctx,
                               Collector<Tuple7<String, String, String, String, String, String, String>> out) throws Exception {
        Node rootNode = parseTree.value();
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        if (rootNode == null) {
            rootNode = new Node();
            File file = new File("src/main/resources/models/parseTree");
            if (file.exists()) {
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                    rootNode = (Node) in.readObject();
                    parseTree.update(rootNode);
                    log.info("parseTree loading is done.");
                } catch (EOFException ignored) {
                }
            }
        }
        String[] regex = new String[]{
                "@[a-z0-9]+$",
                "\\[[A-Za-z0-9\\-\\/]+\\]",
                "\\{.+\\}",
                "(\\d+\\.){3}\\d+",
                "(?<=[^A-Za-z0-9])(\\-?\\+?\\d+)(?=[^A-Za-z0-9])|[0-9]+$"
        };
        int depth = 4;
        int maxChild = 100;
        double st = 0.5;
        LogParser parser = new LogParser(regex, parameterTool.get("logFormat"), depth, maxChild, st);
        DataFrame<String> df_log = parser.load_data(input.f1, parameterTool.get("timeFormat"));
        if (df_log == null) return;
        List<String> logmessageL = Arrays.asList(parser.preprocess(df_log.get(0, "Content")).trim().split("[ ]"));
        LogCluster matchCluster = parser.treeSearch(rootNode, logmessageL);
        if (matchCluster == null) {
            LogCluster newCluster = new LogCluster(logmessageL);
            parser.addSeqToPrefixTree(rootNode, newCluster);
            matchCluster = newCluster;
        } else {
            List<String> oldTemplate = matchCluster.getLogTemplate();
            List<String> newTemplate = parser.getTemplate(logmessageL, matchCluster.getLogTemplate());
            if (!String.join(" ", newTemplate).equals(String.join(" ", oldTemplate))) {
                matchCluster.setLogTemplate(newTemplate);
                TCFGUtil tcfgUtil = new TCFGUtil();
                Map<String, String> templateUpdateRegion = tcfgUtil.getTemplateUpdateRegion();
                templateUpdateRegion.put(parser.getHash(String.join(" ", oldTemplate)), parser.getHash(String.join(" ", newTemplate)));
                tcfgUtil.saveTemplateUpdateRegion(templateUpdateRegion);
//                Map<String, String> t = tcfgUtil.getTemplateUpdateRegion();
//                Iterator<Map.Entry<String, String>> it = t.entrySet().iterator();
//                while(it.hasNext()){
//                    Map.Entry<String, String> entry = it.next();
//                    String mapKey = entry.getKey();
//                    String mapValue = entry.getValue();
//                    System.out.println(mapKey + ":" + mapValue);
//                    it.remove();
//                }
//                tcfgUtil.saveTemplateUpdateRegion(t);
            }
        }
        parseTree.update(rootNode);
        List<String> log_template = new ArrayList<>();
        log_template.add(String.join(" ", matchCluster.getLogTemplate()));
        df_log.add("EventTemplate", log_template);
        String time = df_log.get(0, "Time");
        String level = df_log.get(0, "Level");
        String component = df_log.get(0, "Component");
        String content = df_log.get(0, "Content");
        String eventTemplate = df_log.get(0, "EventTemplate");
        String parameterList = parser.get_parameter_list(df_log);
        parameterList = "\"" + parameterList + "\"";
        String eventID = parser.getHash(eventTemplate);
        Tuple7<String, String, String, String, String, String, String> tuple = new Tuple7<>(time, level, component, content, eventTemplate, parameterList, eventID);
        tuple.f2 = "1";
        templateNum.add(1);
        out.collect(tuple);
//            parser.printTree(rootNode,0);
        if (templateNum.getLocalValue() % 100 == 0) {
            FileWriter oo = new FileWriter(new File("src/main/resources/models/templates.json"));
            parser.saveTemplate(rootNode, 0, oo);
            oo.close();
        }
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<Node> descriptor_parseTree =
                new ValueStateDescriptor<>(
                        "parseTree",
                        Node.class
                );
        parseTree = getRuntimeContext().getState(descriptor_parseTree);
        getRuntimeContext().addAccumulator("templateNum", templateNum);
    }

    @Override
    public void close() throws Exception {
        if (templateNum.getLocalValue() == 0) return;
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(
                new File("src/main/resources/models/parseTree")));
        oo.writeObject(parseTree.value());
        oo.close();
        log.info("parseTree serialization is done.");
    }
}
