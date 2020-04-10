package TCFGmodel;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import humanfeedback.TuningRegion;
import modelconstruction.TransferParamMatrix;
import org.apache.flink.api.java.tuple.Tuple7;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import templatemining.Node;

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
            Properties properties = tcfgUtil.getConfig();
            this.count = 0;
        }

        public int modResult(int interval) {
            count ++;
            count = count%interval;
            return count;
        }
    }

    public Properties getConfig() {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/config.properties"));
            properties.load(bufferedReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
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
        TransferParamMatrix tpm = new TransferParamMatrix();
        TCFG tcfg = new TCFG();
        Map<String, String> templateUpdateRegion = new HashMap<>();
        Node rootNode;
        saveTCFGInMemory(tcfg);
        saveMatrixInMemory(tpm);
        saveTuningRegionInMemory();
        saveTemplateUpdateRegion(templateUpdateRegion);
        try {
            rootNode = getParseTreeRegion();
        } catch (JSONException ignored){
            rootNode = new Node();
        }
        saveParseTreeRegion(rootNode);
        saveTrainingFlag(1);
    }

    public TCFG getTCFGFromMemory() throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        byte[] b = new byte[tcfgSize];
        TCFG.sm.read(1, tcfgSize, b);
        TCFG tcfg = JSONObject.parseObject(new String(b,"utf-8").trim(), TCFG.class);
        return tcfg;
    }
    public void saveTCFGInMemory(TCFG tcfg) throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        String tcfgStr = JSONObject.toJSONString(tcfg);
        TCFG.sm.write(1, tcfgSize, tcfgStr.getBytes("UTF-8"));
    }
    public TransferParamMatrix getMatrixFromMemory() throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.valueOf(properties.getProperty("transferParamMatrixSize"));
        byte[] b = new byte[transferParamMatrixSize];
        TCFG.sm.read(tcfgSize+1, transferParamMatrixSize, b);
        TransferParamMatrix transferParamMatrix = JSONObject.parseObject(new String(b,"utf-8").trim(), TransferParamMatrix.class);
        return transferParamMatrix;
    }
    public void saveMatrixInMemory(TransferParamMatrix transferParamMatrix) throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.valueOf(properties.getProperty("transferParamMatrixSize"));
        String matrixStr = JSONObject.toJSONString(transferParamMatrix);
        TCFG.sm.write(1+tcfgSize, transferParamMatrixSize, matrixStr.getBytes("UTF-8"));
    }
    public void getTuningRegionFromMemory() throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.valueOf(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.valueOf(properties.getProperty("tuningRegionSize"));
        byte[] b = new byte[tuningRegionSize];
        TCFG.sm.read(transferParamMatrixSize+tcfgSize+1, tuningRegionSize, b);
        tuningRegion = JSONObject.parseObject(new String(b,"utf-8").trim(), TuningRegion.class);
    }
    public void saveTuningRegionInMemory() throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.valueOf(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.valueOf(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.valueOf(properties.getProperty("tuningRegionSize"));
        String tuningRegionStr = JSONObject.toJSONString(tuningRegion);
        TCFG.sm.write(1+tcfgSize+transferParamMatrixSize, tuningRegionSize, tuningRegionStr.getBytes("UTF-8"));
    }

    public Map<String, String> getTemplateUpdateRegion() {
        Properties properties = getConfig();
        int tcfgSize = Integer.parseInt(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.parseInt(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.parseInt(properties.getProperty("tuningRegionSize"));
        int templateUpdateRegionSize = Integer.parseInt(properties.getProperty("templateUpdateRegionSize"));
        byte[] b = new byte[templateUpdateRegionSize];
        TCFG.sm.read(1+tcfgSize+transferParamMatrixSize+tuningRegionSize, templateUpdateRegionSize, b);
        return JSONObject.parseObject(b, Map.class);
    }

    public void saveTemplateUpdateRegion(Map<String, String> templateUpdateRegion) throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.parseInt(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.parseInt(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.parseInt(properties.getProperty("tuningRegionSize"));
        int templateUpdateRegionSize = Integer.parseInt(properties.getProperty("templateUpdateRegionSize"));
        String str = JSONObject.toJSONString(templateUpdateRegion);
        TCFG.sm.write(1+tcfgSize+transferParamMatrixSize+tuningRegionSize, templateUpdateRegionSize, str.getBytes("UTF-8"));
    }

    public Node getParseTreeRegion() throws JSONException {
        Properties properties = getConfig();
        int tcfgSize = Integer.parseInt(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.parseInt(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.parseInt(properties.getProperty("tuningRegionSize"));
        int templateUpdateRegionSize = Integer.parseInt(properties.getProperty("templateUpdateRegionSize"));
        int parseTreeRegionSie = Integer.parseInt(properties.getProperty("parseTreeRegionSize"));
        byte[] b = new byte[parseTreeRegionSie];
        System.out.println(TCFG.sm);
        TCFG.sm.read(1+tcfgSize+transferParamMatrixSize+tuningRegionSize+templateUpdateRegionSize, parseTreeRegionSie, b);
        return JSONObject.parseObject(b, Node.class);
    }

    public void saveParseTreeRegion(Node parseTreeRegion) throws Exception{
        Properties properties = getConfig();
        int tcfgSize = Integer.parseInt(properties.getProperty("TCFGSize"));
        int transferParamMatrixSize = Integer.parseInt(properties.getProperty("transferParamMatrixSize"));
        int tuningRegionSize = Integer.parseInt(properties.getProperty("tuningRegionSize"));
        int templateUpdateRegionSize = Integer.parseInt(properties.getProperty("templateUpdateRegionSize"));
        int parseTreeRegionSie = Integer.parseInt(properties.getProperty("parseTreeRegionSize"));
        String str = JSONObject.toJSONString(parseTreeRegion);
        TCFG.sm.write(1+tcfgSize+transferParamMatrixSize+tuningRegionSize+templateUpdateRegionSize, parseTreeRegionSie, str.getBytes("UTF-8"));
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
}
