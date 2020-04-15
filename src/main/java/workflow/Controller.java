package workflow;

import TCFGmodel.TCFG;
import TCFGmodel.TCFGUtil;
import dao.MysqlUtil;

public class Controller {

     int CLEANMEMORY = 0;
     int CLEANDATABASE = 1;
     int CLEANVALUESTATES = 2;
     int ENABLEHUMANFEEDBACK = 3;
     int DISABLEHUMANFEEDBACK = 4;
     int ENABLEANOMALYDETECTION = 5;
     int DISABLEANOMALYDETECTION = 6;

     public int execute(int command) {
         switch (command) {
             case 0:
                 return cleanMemory();
             case 1:
                 return cleanDatabase();
             case 2:
                 return cleanValuestates();
             case 3:
                 return enableHumanFeedback();
             case 4:
                 return disableHumanFeedback();
             case 5:
                 return enableAnomalyDetection();
             case 6:
                 return disableAnomalyDetection();
             default:
                 return -1;
         }
     }

     private int cleanMemory() {
        TCFGUtil tcfgUtil = new TCFGUtil();
        try {
            tcfgUtil.cleanShareMemory();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
     }
     private int cleanDatabase() {
         MysqlUtil mysqlUtil = new MysqlUtil();
         mysqlUtil.truncateTables();
         return 0;
     }
     private int cleanValuestates() {
         Config.valueStates.put("transferParamMatrix", 1);
         Config.valueStates.put("tcfgValueState", 1);
         Config.valueStates.put("parseTree", 1);
         Config.valueStates.put("templateMap", 1);
         return 0;
     }
     private int enableHumanFeedback() {
         TCFGUtil tcfgUtil = new TCFGUtil();
         try {
             tcfgUtil.saveTrainingFlag(0);
         }catch (Exception e) {
             e.printStackTrace();
         }
         return 0;
     }
     private int disableHumanFeedback() {
         TCFGUtil tcfgUtil = new TCFGUtil();
         try {
             tcfgUtil.saveTrainingFlag(1);
         }catch (Exception e) {
             e.printStackTrace();
         }
         return 0;
     }
     private int enableAnomalyDetection() {
         TCFGUtil tcfgUtil = new TCFGUtil();
         try {
             tcfgUtil.saveDetectionFlag(1);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return 0;
     }
     private int disableAnomalyDetection() {
         TCFGUtil tcfgUtil = new TCFGUtil();
         try {
             tcfgUtil.saveDetectionFlag(0);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return 0;
     }

}
