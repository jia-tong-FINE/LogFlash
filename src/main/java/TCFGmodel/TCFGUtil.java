package TCFGmodel;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import dao.MysqlUtil;
import humanfeedback.TuningRegion;
import modelconstruction.TransferParamMatrix;
import org.apache.flink.api.java.tuple.Tuple7;
import java.util.*;

import org.apache.flink.api.java.utils.ParameterTool;
import templatemining.Node;
import workflow.Config;

import static humanfeedback.SuspiciousRegionMonitor.tuningRegion;

public class TCFGUtil {

    public static double tanh(double value) {
        double ex = Math.pow(Math.E, value);// e^x
        double ey = Math.pow(Math.E, -value);//e^(-x)
        double sinhx = ex-ey;
        double coshx = ex+ey;
        double result = sinhx/coshx;
        return result;
    }

    public static  double minMax(double value, double downLimit, double upLimit) {
        return (value-downLimit)/(upLimit-downLimit);
    }

    public static List<Tuple7> deleteReplica(List<Tuple7> list) {
        if (list.size()<2) {
            return list;
        }
        List<Tuple7> tempList = new ArrayList<>();
        for (int i = list.size()- 2; i >=0; i--) {
            boolean flag = false;
            for (int j = 0; j < tempList.size(); j++) {
                if (tempList.get(j).f6.toString().equals(list.get(i).f6.toString())) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                tempList.add(list.get(i));
            }
        }
        List<Tuple7> tempList_reverse = new ArrayList<>();
        for (int i = tempList.size()- 1; i >=0; i--){
            tempList_reverse.add(tempList.get(i));
        }
        tempList_reverse.add(list.get(list.size()-1));
        return tempList_reverse;
    }


    public class counter {
        int count;

        public counter () {
            TCFGUtil tcfgUtil = new TCFGUtil();
            this.count = 0;
        }

        public int modResult(int interval) {
            count ++;
            count = count%interval;
            return count;
        }
    }

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

    public void initiateShareMemory() throws Exception {
        TCFG tcfg = getTCFGFromMemory()==null?new TCFG():getTCFGFromMemory();
        TransferParamMatrix tpm = getMatrixFromMemory()==null?new TransferParamMatrix():getMatrixFromMemory();
        getTuningRegionFromMemory();
        Map<String, String> templateUpdateRegion = getTemplateUpdateRegion()==null?new HashMap<>():getTemplateUpdateRegion();
        Node rootNode = getParseTreeRegion()==null?new Node():getParseTreeRegion();
        saveTCFGInMemory(tcfg);
        saveMatrixInMemory(tpm);
        saveTuningRegionInMemory();
        saveTemplateUpdateRegion(templateUpdateRegion);
        saveParseTreeRegion(rootNode);
        saveTrainingFlag(1);
        saveDetectionFlag(0);
    }
    public void cleanShareMemory() throws Exception {
        TransferParamMatrix tpm = new TransferParamMatrix();
        TCFG tcfg = new TCFG();
        Map<String, String> templateUpdateRegion = new HashMap<>();
        Node rootNode = new Node();
        saveTCFGInMemory(tcfg);
        saveMatrixInMemory(tpm);
        saveTuningRegionInMemory();
        saveTemplateUpdateRegion(templateUpdateRegion);
        saveParseTreeRegion(rootNode);
        saveTrainingFlag(1);
        saveDetectionFlag(0);
    }

    public void initiateDatabase() {
        MysqlUtil mysqlUtil = new MysqlUtil();
        mysqlUtil.createAnomalyLogTable();
        mysqlUtil.createTCFGTable();
    }

    public TCFG getTCFGFromMemory() throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        byte[] b = new byte[tcfgSize];
        TCFG.sm.read(2, tcfgSize, b);
        TCFG tcfg = JSONObject.parseObject(new String(b,"utf-8").trim(), TCFG.class);
        return tcfg;
    }
    public void saveTCFGInMemory(TCFG tcfg) throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        String tcfgStr = JSONObject.toJSONString(tcfg);
        TCFG.sm.write(2, tcfgSize, tcfgStr.getBytes("UTF-8"));
    }
    public TransferParamMatrix getMatrixFromMemory() throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        byte[] b = new byte[transferParamMatrixSize];
        TCFG.sm.read(tcfgSize+2, transferParamMatrixSize, b);
        TransferParamMatrix transferParamMatrix = JSONObject.parseObject(new String(b,"utf-8").trim(), TransferParamMatrix.class);
        return transferParamMatrix;
    }
    public void saveMatrixInMemory(TransferParamMatrix transferParamMatrix) throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        String matrixStr = JSONObject.toJSONString(transferParamMatrix);
        TCFG.sm.write(2+tcfgSize, transferParamMatrixSize, matrixStr.getBytes("UTF-8"));
    }
    public void getTuningRegionFromMemory() throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        byte[] b = new byte[tuningRegionSize];
        TCFG.sm.read(transferParamMatrixSize+tcfgSize+2, tuningRegionSize, b);
        if (JSONObject.parseObject(new String(b,"utf-8").trim(), TuningRegion.class)!=null)
            tuningRegion = JSONObject.parseObject(new String(b,"utf-8").trim(), TuningRegion.class);
    }
    public void saveTuningRegionInMemory() throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        String tuningRegionStr = JSONObject.toJSONString(tuningRegion);
        TCFG.sm.write(2+tcfgSize+transferParamMatrixSize, tuningRegionSize, tuningRegionStr.getBytes("UTF-8"));
    }

    public Map<String, String> getTemplateUpdateRegion() {
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        int templateUpdateRegionSize = parameterTool.getInt("templateUpdateRegionSize");
        byte[] b = new byte[templateUpdateRegionSize];
        TCFG.sm.read(2+tcfgSize+transferParamMatrixSize+tuningRegionSize, templateUpdateRegionSize, b);
        try {
            return JSONObject.parseObject(b, Map.class);
        }
        catch (JSONException e){
            return null;
        }
    }

    public void saveTemplateUpdateRegion(Map<String, String> templateUpdateRegion) throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        int templateUpdateRegionSize = parameterTool.getInt("templateUpdateRegionSize");
        String str = JSONObject.toJSONString(templateUpdateRegion);
        TCFG.sm.write(2+tcfgSize+transferParamMatrixSize+tuningRegionSize, templateUpdateRegionSize, str.getBytes("UTF-8"));
    }

    public Node getParseTreeRegion() throws Exception {
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        int templateUpdateRegionSize = parameterTool.getInt("templateUpdateRegionSize");
        int parseTreeRegionSie = parameterTool.getInt("parseTreeRegionSize");
        byte[] b = new byte[parseTreeRegionSie];
        TCFG.sm.read(2+tcfgSize+transferParamMatrixSize+tuningRegionSize+templateUpdateRegionSize, parseTreeRegionSie, b);
        return JSONObject.parseObject(new String(b,"utf-8").trim(), Node.class);
    }

    public void saveParseTreeRegion(Node parseTreeRegion) throws Exception{
        ParameterTool parameterTool = ParameterTool.fromMap(Config.parameter);
        int tcfgSize = parameterTool.getInt("TCFGSize");
        int transferParamMatrixSize = parameterTool.getInt("transferParamMatrixSize");
        int tuningRegionSize = parameterTool.getInt("tuningRegionSize");
        int templateUpdateRegionSize = parameterTool.getInt("templateUpdateRegionSize");
        int parseTreeRegionSie = parameterTool.getInt("parseTreeRegionSize");
        String str = JSONObject.toJSONString(parseTreeRegion);
        TCFG.sm.write(2+tcfgSize+transferParamMatrixSize+tuningRegionSize+templateUpdateRegionSize, parseTreeRegionSie, str.getBytes("UTF-8"));
    }

    public int getTrainingFlag() {
        byte[] b = new byte[1];
        TCFG.sm.read(0, 1, b);
        return JSONObject.parseObject(b, Integer.class);
    }

    public void saveTrainingFlag(int flag) throws Exception{
        String trainingFlag = JSONObject.toJSONString(flag);
        TCFG.sm.write(0, 1, trainingFlag.getBytes("UTF-8"));
    }

    public int getDetectionFlag() {
        byte[] b = new byte[1];
        TCFG.sm.read(1, 1, b);
        return JSONObject.parseObject(b, Integer.class);
    }

    public void saveDetectionFlag(int flag) throws Exception{
        String trainingFlag = JSONObject.toJSONString(flag);
        TCFG.sm.write(1, 1, trainingFlag.getBytes("UTF-8"));
    }
}
