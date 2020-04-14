package workflow;

import com.alibaba.fastjson.JSON;
import dao.MysqlUtil;
import faultdiagnosis.Anomaly;
import humanfeedback.SuspiciousRegionMonitor;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.Map;

import static spark.Spark.port;

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
        String CommandList = request.queryParams("commands");
        Map CommandMap = JSON.parseObject(CommandList,Map.class);
        //parse CommandMap and execute system updates with workflow.Controller
        response.status(200);
        return "OK";
    };

    @Override
    public void run() {
        port(30811);
        Spark.post("/AnomalyID", postAnomalyID);
        Spark.post("/Config", postConfig);
        Spark.post("/CommandList", postCommands);
    }

    public void cancel() {
        Spark.stop();
    }

}
