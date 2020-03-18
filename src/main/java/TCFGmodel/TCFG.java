package TCFGmodel;

import modelconstruction.TransferParamMatrix;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Time-weighted Control Flow Graph(TCFG) Model
 */

public class TCFG {
    public static ShareMemory sm;

    private List<Node> nodes;
    private List<Edge> edges;

    public TCFG() {

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }


    public class Node {
        String node_id;
        String node_name;
        String template;
        int frequency;

        public String getNode_id() {
            return node_id;
        }
    }

    public class Edge {
        Node in_node;
        Node out_node;
        long time_weight;

        public Node getIn_node() {
            return in_node;
        }

        public Node getOut_node() {
            return out_node;
        }

        public long getTime_weight() {
            return time_weight;
        }
    }

    private boolean ifInNodeList(Node judgeNode) {
        boolean flag = false;
        for (Node node: nodes) {
            if (judgeNode.node_id.equals(node.node_id)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    public void paramMatrix2TCFG (TransferParamMatrix transferParamMatrix, long delta) {

        Map<String, Map<String, Double>> paramMatrix = transferParamMatrix.getParamMatrix();
        Map<String, Map<String, Long>> timeMatrix = transferParamMatrix.getTimeMatrix();
        TCFGUtil tcfgUtil = new TCFGUtil();
        for (String key1: paramMatrix.keySet()) {
            TCFG.Node in_node = new TCFG.Node();
            in_node.node_id = key1;
            if (!ifInNodeList(in_node)) {
                nodes.add(in_node);
            }
            for (String key2: paramMatrix.get(key1).keySet()) {
                TCFG.Node out_node = new TCFG.Node();
                out_node.node_id = key2;
                if (!ifInNodeList(out_node)) {
                    nodes.add(out_node);
                }
                double alphaji = paramMatrix.get(key1).get(key2);
                double transitionProb = tcfgUtil.calDefinitIntegral(delta, 2*delta, 100, alphaji, delta);
                if (transitionProb > 0.1) {
                    TCFG.Edge edge = new TCFG.Edge();
                    edge.in_node = in_node;
                    edge.out_node = out_node;
                    edge.time_weight = timeMatrix.get(in_node.node_id).get(out_node.node_id);
                    edges.add(edge);
                }
            }
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public static void main() {

    }
}
