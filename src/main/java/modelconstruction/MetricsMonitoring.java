package modelconstruction;

import TCFGmodel.TCFGUtil;

import java.util.LinkedList;
import java.util.Queue;

public class MetricsMonitoring extends Thread {

    private boolean flag = true;
    private TCFGUtil tcfgUtil = new TCFGUtil();
    MovingVariance m = new MovingVariance(10);

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(300);
                TransferParamMatrix transferParamMatrix = tcfgUtil.getMatrixFromMemory();
                if (transferParamMatrix == null) continue;
                double norm = transferParamMatrix.getNorm();
                double var = m.add(norm);
                if (var != 0.0 && var < 0.01) tcfgUtil.saveTrainingFlag(0);
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
};