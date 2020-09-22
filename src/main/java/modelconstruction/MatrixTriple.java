package modelconstruction;

public class MatrixTriple{

    double value;
    long timeWeight;


    public MatrixTriple(double value, long timeWeight) {
        this.value = value;
        this.timeWeight = timeWeight;
    }


    public void setValue(double value) {
        this.value = value;
    }

    public void setTimeWeight(long timeWeight) {
        this.timeWeight = timeWeight;
    }

    public double getValue() {
        return value;
    }

    public long getTimeWeight() {
        return timeWeight;
    }
}
