package humanfeedback;

import dao.MysqlUtil;
import faultdiagnosis.Anomaly;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import static spark.Spark.port;

public class FeedbackListener extends Thread {

    // Intent:
    //创建一个http请求的监听线程，接收前端向该线程发送anomalyID
    //然后从数据库中获取anomaly的最后一列信息，将该jsontext其转换为Anomaly对象
    //调用SuspiciousRegionMonitor.feedBackFalseAlarms.addAnomalyToFalseAlarms()将该对象加入队列里供其他的线程使用

    // Usage:
    // FeedbackListener listener = new FeedbackListener();
    // listener.start();

    private MysqlUtil sql = new MysqlUtil();

    public Route serveAnomalyID = (Request request, Response response) -> {
        response.header("Access-Control-Allow-Origin", "*");
        String id = request.queryParams("id");
        // Anomaly a = new Anomaly("123", new Tuple7("11", "11", "11","11","11","11","11"), new ArrayList<>(), new ArrayList<>(), "Error");
        Anomaly anomaly = sql.getAnomalyByID(Integer.parseInt(id));
        SuspiciousRegionMonitor.feedBackFalseAlarms.addAnomalyToFalseAlarms(anomaly);
        response.status(200);
        return "OK";
    };

    @Override
    public void run() {
        port(811);
        Spark.post("/", serveAnomalyID);
    }
}
