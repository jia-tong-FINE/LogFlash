package humanfeedback;

public class FeedbackListener {
    //创建一个http请求的监听线程，接收前端向该线程发送anomalyID
    //然后从数据库中获取anomaly的最后一列信息，将该jsontext其转换为Anomaly对象
    //调用SuspiciousRegionMonitor.feedBackFalseAlarms.addAnomalyToFalseAlarms()将该对象加入队列里供其他的线程使用

}
