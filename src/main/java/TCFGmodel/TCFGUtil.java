package TCFGmodel;


import com.alibaba.fastjson.JSONObject;
import modelconstruction.TransferParamMatrix;
import org.apache.flink.api.java.tuple.Tuple7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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


    public static class counter {
        int count;
        int interval;

        public counter () {
            TCFGUtil tcfgUtil = new TCFGUtil();
            Properties properties = tcfgUtil.getConfig();
            this.count = 0;
            this.interval = Integer.valueOf(properties.getProperty("interval"));
        }

        public int modResult(int interval) {
            count = count%interval;
            count ++;
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

//    public TCFG getTCFGFromMemory() {
//        Properties properties = getConfig();
//        int tcfgSize = Long.parseLong(properties.getProperty("TCFGSize"));
//        byte[] b = new byte[tcfgSize];
//        TCFG.sm.read(1, tcfgSize, b);
//        return (JSONObject.parseObject(b, TCFG.class));
//    }

}
