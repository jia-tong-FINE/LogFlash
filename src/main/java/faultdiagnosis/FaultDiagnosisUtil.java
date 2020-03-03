package faultdiagnosis;

public class FaultDiagnosisUtil {

    private double calProbabilityMode1(double x, double alphaji, long delta) {
        double prob = (alphaji/delta)* Math.pow((x)/(double)delta,-1-alphaji);
        return prob;
    }

    public double calDefinitIntegral(double a, double b, int blocks, double alphaji, long delta) {

        double sum = 0;
        double e = (b - a) / (double)blocks;
        for (int i = 1; i <= blocks; i++) {
            double midResult = a + (double) i * (b - a) / (double) blocks;
            sum = sum + calProbabilityMode1(midResult, alphaji, delta);
            //System.out.println(calProbabilityMode1(midResult, alphaji, delta));
        }
        //System.out.println(sum);
        return sum*e;
    }

}
