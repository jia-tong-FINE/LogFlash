package workflow;

import TCFGmodel.TCFGUtil;
import com.alibaba.fastjson.JSON;
import dao.AnomalyJSON;
import dao.MysqlUtil;
import faultdiagnosis.Anomaly;
import humanfeedback.SuspiciousRegionMonitor;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandListener extends Thread {

    // Usage:
    // CommandListener listener = new FeedbackListener();
    // listener.start();

    private MysqlUtil sql = new MysqlUtil();

    public Route postAnomalyID = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        String id = request.splat()[0];
        Anomaly anomaly = sql.getAnomalyByID(Integer.parseInt(id));
        SuspiciousRegionMonitor.feedBackFalseAlarms.addAnomalyToFalseAlarms(anomaly);
        response.status(200);
        return "OK";
    };
    public Route postConfig = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        String config = request.body();
        //update system configuration
        Map<String, Object> configMap = JSON.parseObject(config,Map.class);
        for (Map.Entry<String, Object> obj : configMap.entrySet()){
            Config.parameter.put(obj.getKey(), obj.getValue().toString());
        }
        response.status(200);
        Map<String, Map<String, String>> ret = new HashMap<>();
        ret.put("Parameter", Config.parameter);
        return JSON.toJSONString(ret);
    };
    public Route postCommands = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        //parse CommandStr and execute system updates with workflow.Controller
        String commandStr = request.body();
        Map commandMap = JSON.parseObject(commandStr, Map.class);
        Controller controller = new Controller();
        controller.executeCommands(commandMap);
        response.status(200);
        TCFGUtil tcfgUtil = new TCFGUtil();
        Map<String, Integer> ret = new HashMap<>();
        ret.put("Detection", tcfgUtil.getDetectionFlag());
        ret.put("Training", tcfgUtil.getTrainingFlag());
        return JSON.toJSONString(ret);
    };

    public Route getConfig = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        Map<String, Map<String, String>> ret = new HashMap<>();
        ret.put("Parameter", Config.parameter);
        ret.put("ValueStates", Config.parameter);
        String json = JSON.toJSONString(ret);
        System.out.println(json);
        return json;
    };

    public Route getCommands = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        TCFGUtil tcfgUtil = new TCFGUtil();
        Map<String, Integer> ret = new HashMap<>();
        ret.put("Detection", tcfgUtil.getDetectionFlag());
        ret.put("Training", tcfgUtil.getTrainingFlag());
        String json = JSON.toJSONString(ret);
        System.out.println(json);
        return json;
    };

    public Route getTCFG = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        // get TCFG data
        response.type("application/json");
        return sql.getTCFG();
    };

    public Route getAnomalies = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        // get anomalies data
        response.type("application/json");
        List<AnomalyJSON> res = sql.getAnomalies();
        return JSON.toJSONString(res);
    };

    @Override
    public void run() {
        Spark.port(30822);
        Spark.post("/Anomaly/*", postAnomalyID);
        Spark.patch("/Config", postConfig);
        Spark.patch("/CommandList", postCommands);
        Spark.get("/Config", getConfig);
        Spark.get("/CommandList", getCommands);
        Spark.get("/TCFG", getTCFG);
        Spark.get("/Anomalies", getAnomalies);
    }

    public void cancel() {
        Spark.stop();
    }

}
