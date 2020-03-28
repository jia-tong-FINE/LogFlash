package modelconstruction;

import TCFGmodel.TCFGUtil;

public class MetricsMonitoring extends Thread {

    private boolean flag = true;
    private TCFGUtil tcfgUtil = new TCFGUtil();
    private Double preNorm = 0.0;
    private int num = 0;

    @Override
    public void run() {
        while (flag) {
            try {
                Thread.sleep(500);
                TransferParamMatrix transferParamMatrix = tcfgUtil.getMatrixFromMemory();
                Double norm = transferParamMatrix.getNorm();
                if (preNorm != 0 && Math.abs(norm - preNorm) < 0.5) num++;
                else num = 0;
                if (num == 5) {
                    TCFGUtil tcfgUtil = new TCFGUtil();
                    tcfgUtil.saveTrainingFlag(0);
                    System.out.println("stop training!");
                }
                preNorm = norm;
                System.out.println(norm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        this.flag = false;
    }
}
