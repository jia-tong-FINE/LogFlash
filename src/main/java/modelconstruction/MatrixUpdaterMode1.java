package modelconstruction;

import org.apache.flink.api.java.tuple.Tuple7;
import java.util.List;


public class MatrixUpdaterMode1 implements MatrixUpdater {

    @Override
    public double calGradientForInfected(long a, long b, TransferParamMatrix c, List<Tuple7> d, long e, long f) {
        return 0;
    }

    @Override
    public double calGradientForUninfected(long a, long b, TransferParamMatrix c, List<Tuple7> d, long e) {
        return 0;
    }
}
