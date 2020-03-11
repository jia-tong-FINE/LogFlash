package dao;

import faultdiagnosis.Anomaly;
import org.apache.flink.api.java.tuple.Tuple7;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class MysqlUtil {

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/anomalies?useSSL=false&serverTimezone=UTC";

    // 数据库的用户名与密码
    static final String USER = "root";
    static final String PASS = "jt1118961";

    private String StamptoTime(String time, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        time = formatter.format(Long.valueOf(time));
        return time;
    }

    public void insertAnomaly(Anomaly anomaly) {

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            // 执行查询
            stmt = conn.createStatement();
            String sql = "insert into anomaly_log (time,unixtime,level,component,content,template,paramlist,eventid,anomalylogs,anomalyrequest,anomalywindow,anomalytype,anomalytemplates) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            List anomalylogslist = anomaly.getAnomalyLogList();
            String anomalytype = anomaly.getAnomalyType();
            Tuple7 logcontent = anomaly.getAnomalyLog();
            List anomalyrequestlist = anomaly.getSuspectedAnomalyRequest();
            String unixtime = (String)logcontent.f0;
            String time = StamptoTime(unixtime, "HH:mm:ss:SSS");
            String level = (String)logcontent.f1;
            String component = (String)logcontent.f2;
            String content = (String)logcontent.f3;
            String template = (String)logcontent.f4;
            String paramlist = (String)logcontent.f5;
            String eventid = (String)logcontent.f6;
            String anomalylogs = "";
            for (Object templog: anomalylogslist) {
                Tuple7 log = (Tuple7)templog;
                anomalylogs = anomalylogs + log.f3 + '\n';
            }
            String anomalyrequest = "";
            for (Object templog: anomalyrequestlist) {
                Tuple7 log = (Tuple7)templog;
                anomalyrequest = anomalyrequest + log.f3 + '\n';
            }
            String anomalyrequesttemplates = "";
            for (Object templog: anomalyrequestlist) {
                Tuple7 log = (Tuple7)templog;
                anomalyrequesttemplates = anomalyrequesttemplates + log.f6 + '\n';
            }
            String anomalywindow = "";
            ps.setString(1,time);
            ps.setString(2,unixtime);
            ps.setString(3,level);
            ps.setString(4,component);
            ps.setString(5,content);
            ps.setString(6,template);
            ps.setString(7,paramlist);
            ps.setString(8,eventid);
            ps.setString(9,anomalylogs);
            ps.setString(10,anomalyrequest);
            ps.setString(11,anomalywindow);
            ps.setString(12,anomalytype);
            ps.setString(13,anomalyrequesttemplates);
            ps.executeUpdate();

            // 完成后关闭
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    public Anomaly getAnomalyByID(int id) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            // 执行查询
            stmt = conn.createStatement();
            String sql = "SELECT * FROM anomaly WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return null;
    }

    public String getFailureTypeByFaultId(String faultId) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            // 执行查询
            stmt = conn.createStatement();
            String sql = "SELECT failure_type FROM injection_record_hadoop WHERE fault_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, faultId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String faultType = rs.getString(1);
                return faultType;
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return null;
    }

    public String getActiviatFlagByFaultId(String faultId) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            // 执行查询
            stmt = conn.createStatement();
            String sql = "SELECT activated FROM injection_record_hadoop WHERE fault_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, faultId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String faultType = rs.getString(1);
                return faultType;
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return null;
    }

}
