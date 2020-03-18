package modelconstruction;

import org.apache.flink.api.java.tuple.Tuple7;

import java.util.List;

public interface MatrixUpdater {

    double calGradientForInfected(long a, long b, TransferParamMatrix c, List<Tuple7> d, long e, long f);

    double calGradientForUninfected(long a, long b, TransferParamMatrix c, List<Tuple7> d, long e);


}
