package modelconstruction;

import TCFGmodel.TCFGUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MetricsMonitoring extends Thread {

    private boolean flag = true;
    private final TCFGUtil tcfgUtil = new TCFGUtil();
    private final Logger LOG = LoggerFactory.getLogger(MetricsMonitoring.class);
    MovingVariance m = new MovingVariance(10);

    @Override
    public void run() {
        try {
            flag = Boolean.parseBoolean(ParameterTool.fromPropertiesFile("src/main/resources/config.properties").toMap().get("metricsMonitoring"));
            LOG.info("{}",flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (flag) {
            try {
                Thread.sleep(300);
                TransferParamMatrix transferParamMatrix = tcfgUtil.getMatrixFromMemory();
                if (transferParamMatrix == null) continue;
                double norm = transferParamMatrix.getNorm();
                double var = m.add(norm);
                if (var != 0.0 && var < 0.005) {
                    tcfgUtil.saveTrainingFlag(0);
                    tcfgUtil.saveDetectionFlag(1);
                    System.out.println("反馈机制已开启！");
                    System.out.println("异常检测已开启！");
                    LOG.info("反馈机制已开启！");
                    LOG.info("异常检测已开启！");
                    cancel();
                }
            } catch (NullPointerException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        flag = false;
    }
}

class MovingVariance {
    int size;
    Queue<Double> q = new LinkedList<>();
    double sum = 0;
    double avg = 0;
    double var = 0;

    MovingVariance(int size) {
        this.size = size;
    }

    double add(double val) {
        if (q.size() >= size) {
            sum -= q.element();
            q.poll();
        }
        q.offer(val);
        sum += val;
        avg = sum / q.size();
        var = 0;
        for (Double x : q) {
            var += Math.pow((x - avg), 2);
        }
        return var / q.size();
    }
}