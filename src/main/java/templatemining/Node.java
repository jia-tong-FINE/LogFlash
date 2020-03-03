package templatemining;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node implements Serializable {
    private int depth;
    private Map<String, Node> childD;
    private List<LogCluster> childLG;
    private String digitOrtoken;

    Node() {
        childD = new HashMap<>();
        childLG = new ArrayList<>();
        depth = 0;
    }

    Node(int depth, String digitOrtoken) {
        childD = new HashMap<>();
        childLG = new ArrayList<>();
        this.depth = depth;
        this.digitOrtoken = digitOrtoken;
    }

    Map<String, Node> getChildD() {
        return childD;
    }

    List<LogCluster> getChildLG() {
        return childLG;
    }

    int getDepth() {
        return depth;
    }

    String getDigitOrtoken() {
        return digitOrtoken;
    }

    void setChildD(String seqLen, Node node) {
        childD.put(seqLen, node);
    }

    void setChildLG(LogCluster child) {
        childLG.add(child);
    }
}
