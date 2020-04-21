package workflow;

import com.alibaba.fastjson.JSON;
import dao.AnomalyJSON;
import dao.MysqlUtil;
import faultdiagnosis.Anomaly;
import humanfeedback.SuspiciousRegionMonitor;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.List;
import java.util.Map;

public class CommandListener extends Thread {

    // Usage:
    // CommandListener listener = new FeedbackListener();
    // listener.start();

    private MysqlUtil sql = new MysqlUtil();

    public Route postAnomalyID = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        String id = request.queryParams("id");
        Anomaly anomaly = sql.getAnomalyByID(Integer.parseInt(id));
        SuspiciousRegionMonitor.feedBackFalseAlarms.addAnomalyToFalseAlarms(anomaly);
        response.status(200);
        return "OK";
    };
    public Route postConfig = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        String config = request.queryParams("config");
        //update system configuration
        Map<String, Object> configMap = JSON.parseObject(config,Map.class);
        for (Map.Entry<String, Object> obj : configMap.entrySet()){
            Config.parameter.put(obj.getKey(), obj.getValue().toString());
        }
        response.status(200);
        return "OK";
    };
    public Route postCommands = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        //parse CommandStr and execute system updates with workflow.Controller
        String commandStr = request.queryParams("commands");
        Map commandMap = JSON.parseObject(commandStr, Map.class);
        Controller controller = new Controller();
        controller.executeCommands(commandMap);
        response.status(200);
        return "OK";
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
        Spark.post("/AnomalyID", postAnomalyID);
        Spark.post("/Config", postConfig);
        Spark.post("/CommandList", postCommands);
        Spark.get("/TCFG", getTCFG);
        Spark.get("/Anomalies", getAnomalies);
    }

    public void cancel() {
        Spark.stop();
    }

}
