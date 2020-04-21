package dao;

import com.alibaba.fastjson.annotation.JSONField;

public class AnomalyJSON {
    @JSONField(name = "id")
    public int id;
    @JSONField(name = "time")
    public String time;
    @JSONField(name = "unix_time")
    public String unix_time;
    @JSONField(name = "level")
    public String level;
    @JSONField(name = "component")
    public String component;
    @JSONField(name = "content")
    public String content;
    @JSONField(name = "template")
    public String template;
    @JSONField(name = "param_list")
    public String param_list;
    @JSONField(name = "event_id")
    public String event_id;
    @JSONField(name = "anomaly_logs")
    public String anomaly_logs;
    @JSONField(name = "anomaly_request")
    public String anomaly_request;
    @JSONField(name = "anomaly_window")
    public String anomaly_window;
    @JSONField(name = "anomaly_type")
    public String anomaly_type;
    @JSONField(name = "anomaly_templates")
    public String anomaly_templates;

    public AnomalyJSON(int id, String time, String unix_time, String level, String component, String content, String template, String param_list, String event_id, String anomaly_logs, String anomaly_request, String anomaly_window, String anomaly_type, String anomaly_templates) {
        this.id = id;
        this.time = time;
        this.unix_time = unix_time;
        this.level = level;
        this.component = component;
        this.content = content;
        this.template = template;
        this.param_list = param_list;
        this.event_id = event_id;
        this.anomaly_logs = anomaly_logs;
        this.anomaly_request = anomaly_request;
        this.anomaly_window = anomaly_window;
        this.anomaly_type = anomaly_type;
        this.anomaly_templates = anomaly_templates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUnix_time() {
        return unix_time;
    }

    public void setUnix_time(String unix_time) {
        this.unix_time = unix_time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getParam_list() {
        return param_list;
    }

    public void setParam_list(String param_list) {
        this.param_list = param_list;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getAnomaly_logs() {
        return anomaly_logs;
    }

    public void setAnomaly_logs(String anomaly_logs) {
        this.anomaly_logs = anomaly_logs;
    }

    public String getAnomaly_request() {
        return anomaly_request;
    }

    public void setAnomaly_request(String anomaly_request) {
        this.anomaly_request = anomaly_request;
    }

    public String getAnomaly_window() {
        return anomaly_window;
    }

    public void setAnomaly_window(String anomaly_window) {
        this.anomaly_window = anomaly_window;
    }

    public String getAnomaly_type() {
        return anomaly_type;
    }

    public void setAnomaly_type(String anomaly_type) {
        this.anomaly_type = anomaly_type;
    }

    public String getAnomaly_templates() {
        return anomaly_templates;
    }

    public void setAnomaly_templates(String anomaly_templates) {
        this.anomaly_templates = anomaly_templates;
    }
}
