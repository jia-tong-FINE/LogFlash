package modelconstruction;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Transfer Parameter Matrix
public class TransferParamMatrix implements Serializable {

    private List<String> eventIDList;
    private Map<String, String> eventIDandContent;
    private Map<String, Map<String, Double>> paramMatrix;
    private Map<String, Map<String, Double>> gradMatrix;
    private Map<String, Map<String, Long>> timeMatrix;

    TransferParamMatrix() {
        eventIDList = new ArrayList<>();
        eventIDandContent = new HashMap<>();
        paramMatrix = new HashMap<>();
        gradMatrix = new HashMap<>();
        timeMatrix = new HashMap<>();
    }

    //Update the parameter matrix with gradient matrix
    //gamma is the update step length
    public void updateParamMatrix(double gamma){
        for (String alphai : gradMatrix.keySet()) {
            Map<String, Double> tempMap = paramMatrix.get(alphai);
            for (String alphaj : gradMatrix.get(alphai).keySet()) {
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                Double tempParam = paramMatrix.get(alphai).get(alphaj);
                Double tempGrad = gradMatrix.get(alphai).get(alphaj);
                double result = tempParam.doubleValue() - gamma * tempGrad.doubleValue();
                if (result < 0) {
                    result = 0;
                }
                tempMap.put(alphaj, Double.valueOf(result));
            }
            paramMatrix.put(alphai,tempMap);
        }
    }

    public void updateTimeMatrix(String eventi, String eventj, long timeWeight) {
        if (timeMatrix.get(eventj).get(eventi) >= timeWeight) {
            return;
        }
        Map<String, Long> tempTimeColumn = timeMatrix.get(eventj);
        tempTimeColumn.put(eventi, Long.valueOf(timeWeight));
        timeMatrix.put(eventj,tempTimeColumn);
    }

    //check the consistency of paramMatrix and gradMatrix
    public boolean checkConsistency() {
        boolean flag = true;
        try {
            if (paramMatrix.size() != gradMatrix.size()) {
                flag = false;
            }
        }catch (Exception e) {
        }
        return flag;
    }

    public void updateGradMatrix(String eventi, String eventj, double gradient) {
        if (gradient == 0) {
            return;
        }
        if ((gradMatrix.get(eventj).get(eventi).doubleValue() != 0) && (gradient <= gradMatrix.get(eventj).get(eventi).doubleValue())) {
            return;
        }
        double newGradient = gradient;
        Map<String, Double> tempGradColumn = gradMatrix.get(eventj);
        tempGradColumn.put(eventi, Double.valueOf(newGradient));
        gradMatrix.put(eventj,tempGradColumn);
    }

    public void clearGradMatrix() {
        for (String alphai : gradMatrix.keySet()) {
            Map<String, Double> tempMap = gradMatrix.get(alphai);
            for (String alphaj : gradMatrix.get(alphai).keySet()) {
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                tempMap.put(alphaj, Double.valueOf(0));
            }
            gradMatrix.put(alphai,tempMap);
        }
    }

    //add new template into the paramMatrix and gradMatrix
    public void addNewTemplate(String EventID, String EventContent, Double alpha) {
        eventIDList.add(EventID);
        eventIDandContent.put(EventID,EventContent);
        Map<String, Double> newParamColumn = new HashMap<>();
        Map<String, Double> newGradColumn = new HashMap<>();
        Map<String, Long> newTimeColumn = new HashMap<>();
        for (String key: paramMatrix.keySet()) {
            newParamColumn.put(key,alpha);
            newGradColumn.put(key, Double.valueOf(0));
            newTimeColumn.put(key, Long.valueOf(0));

            Map<String, Double> tempParamColumn = paramMatrix.get(key);
            tempParamColumn.put(EventID,alpha);
            paramMatrix.put(key,tempParamColumn);

            Map<String, Double> tempGradColumn = gradMatrix.get(key);
            tempGradColumn.put(EventID, Double.valueOf(0));
            gradMatrix.put(key,tempGradColumn);

            Map<String, Long> tempTimeColumn = timeMatrix.get(key);
            tempTimeColumn.put(EventID, Long.valueOf(0));
            timeMatrix.put(key,tempTimeColumn);
        }
        newParamColumn.put(EventID,alpha);
        newGradColumn.put(EventID, Double.valueOf(0));
        newTimeColumn.put(EventID, Long.valueOf(0));
        paramMatrix.put(EventID,newParamColumn);
        gradMatrix.put(EventID,newGradColumn);
        timeMatrix.put(EventID,newTimeColumn);
    }

    public void decay(double beta) {
        for (String alphai : paramMatrix.keySet()) {
            Map<String, Double> tempMap = paramMatrix.get(alphai);
            for (String alphaj : paramMatrix.get(alphai).keySet()) {
                if (paramMatrix.get(alphai).get(alphaj).doubleValue() == 0.0) {
                    continue;
                }
                if (gradMatrix.get(alphai).get(alphaj).doubleValue() != 0.0) {
                    continue;
                }
                Double tempParam = paramMatrix.get(alphai).get(alphaj);
                double result = tempParam * beta;
                tempMap.put(alphaj, Double.valueOf(result));
            }
            paramMatrix.put(alphai, tempMap);
        }
    }

    public String saveParamMatrix(String urlPath, String[] header) {

        List list = new ArrayList<>();
        list.add(eventIDandContent);
        list.add(paramMatrix);
        String paramMatrixJSON = JSONObject.toJSONString(list);
//        System.out.println(paramMatrixJSON);
            String status = "";

            String responseStr = "";

            PrintWriter out = null;

            BufferedReader in = null;

            try {

                URL realUrl = new URL(urlPath);

                // 打开和URL之间的连接

                URLConnection conn = realUrl.openConnection();

                HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;

                // 设置请求属性

                httpUrlConnection.setRequestProperty("Content-Type", "application/json");

                httpUrlConnection.setRequestProperty("x-adviewrtb-version", "2.1");

                // 发送POST请求必须设置如下两行

                httpUrlConnection.setDoOutput(true);

                httpUrlConnection.setDoInput(true);

                // 获取URLConnection对象对应的输出流

                out = new PrintWriter(httpUrlConnection.getOutputStream());

                // 发送请求参数

                out.write(paramMatrixJSON);

                // flush输出流的缓冲

                out.flush();

                httpUrlConnection.connect();

                // 定义BufferedReader输入流来读取URL的响应

                in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));

                String line;

                while ((line = in.readLine()) != null) {

                    responseStr += line;

                }

                status = new Integer(httpUrlConnection.getResponseCode()).toString();


            } catch (Exception e) {

                System.out.println("发送 POST 请求出现异常！" + e);

            }

            // 使用finally块来关闭输出流、输入流

            finally {

                try {

                    if (out != null) { out.close();}

                    if (in != null) {in.close();}

                } catch (Exception ex) {

                    ex.printStackTrace();

                }

            }

            return responseStr;


    }

    public List<String> getEventIDList() {
        return eventIDList;
    }

    public void setEventIDList(List<String> eventIDList) {
        this.eventIDList = eventIDList;
    }

    public Map<String, Map<String, Double>> getParamMatrix() {
        return paramMatrix;
    }

    public void setParamMatrix(Map<String, Map<String, Double>> paramMatrix) {
        this.paramMatrix = paramMatrix;
    }

    public Map<String, Map<String, Double>> getGradMatrix() {
        return gradMatrix;
    }

    public void setGradMatrix(Map<String, Map<String, Double>> gradMatrix) {
        this.gradMatrix = gradMatrix;
    }

    public Map<String, String> getEventIDandContent() {
        return eventIDandContent;
    }

    public void setEventIDandContent(Map<String, String> eventIDandContent) {
        this.eventIDandContent = eventIDandContent;
    }

    public Map<String, Map<String, Long>> getTimeMatrix() {
        return timeMatrix;
    }
}
