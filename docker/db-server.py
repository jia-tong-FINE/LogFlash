import json
import logging
import time
import traceback

import pymysql
from flask import Flask, jsonify, request

logging.basicConfig(level=logging.DEBUG)

db_conf = {
    'host': 'localhost',
    'user': 'root',
    'password': 'jt1118961',
    'database': 'anomalies'
}

try:
    with open('db-config.json', 'r') as f:
        db_conf = json.load(f)
        logging.info("Using configuration from json.")
except Exception:
    logging.warning("Config json not found, using default values.")

app = Flask(__name__)


@app.route('/Anomaly/Count', methods=['GET'])
def query_total():
    try:
        db = pymysql.connect(host=db_conf['host'], user=db_conf['user'],
                             password=db_conf['password'], database=db_conf['database'])
        cursor = db.cursor()
        sql = '''SELECT count(*) FROM anomaly_log'''
        cursor.execute(sql)
    except Exception:
        traceback.print_exc()
        return None
    result = cursor.fetchone()
    return jsonify({'count': result[0]})


@app.route('/Anomaly', methods=['GET'])
def query_anomalies():
    pageNum = request.args.get('page', '1')
    pageSize = request.args.get('size', '20')
    sortProperty = request.args.get('sortProperty', 'id')
    num = int(pageNum) - 1
    flag = num * int(pageSize)
    try:
        db = pymysql.connect(host=db_conf['host'], user=db_conf['user'],
                             password=db_conf['password'], database=db_conf['database'])
        cursor = db.cursor()
        sql = '''SELECT id,time,unixtime,level,component,content,template,paramlist,eventid,anomalylogs,anomalyrequest,anomalywindow,anomalytype,anomalytemplates,logsequence_json FROM anomaly_log ORDER BY %s LIMIT %d, %d '''
        cursor.execute(sql % (sortProperty, flag, int(pageSize)))

    except Exception:
        traceback.print_exc()
        return None
    results = cursor.fetchall()
    data = []
    for row in results:
        data.append({
            'id': row[0],
            'time': row[1],
            'unix_time': row[2],
            'level': row[3],
            'component': row[4],
            'content': row[5],
            'template': row[6],
            'param_list': row[7],
            'event_id': row[8],
            'anomaly_logs': row[9],
            'anomaly_request': row[10],
            'anomaly_window': row[11],
            'anomaly_type': row[12],
            'anomaly_templates': row[13],
            'logsequence_json': row[14]
        })
    return jsonify(data)


@app.route('/Anomaly/<int:aid>', methods=['GET'])
def query_anomaly(aid):
    try:
        db = pymysql.connect(host=db_conf['host'], user=db_conf['user'],
                             password=db_conf['password'], database=db_conf['database'])
        cursor = db.cursor()
        sql = '''SELECT id,time,unixtime,level,component,content,template,paramlist,eventid,anomalylogs,anomalyrequest,anomalywindow,anomalytype,anomalytemplates,logsequence_json FROM anomaly_log WHERE id = %d'''
        cursor.execute(sql % aid)
    except Exception:
        traceback.print_exc()
        return None
    result = cursor.fetchone()
    return jsonify({
        'id': result[0],
        'time': result[1],
        'unix_time': result[2],
        'level': result[3],
        'component': result[4],
        'content': result[5],
        'template': result[6],
        'param_list': result[7],
        'event_id': result[8],
        'anomaly_logs': result[9],
        'anomaly_request': result[10],
        'anomaly_window': result[11],
        'anomaly_type': result[12],
        'anomaly_templates': result[13],
        'logsequence_json': result[14]
    })


@app.route('/TCFG', methods=['GET'])
def query_tcfg():
    try:
        db = pymysql.connect(host=db_conf['host'], user=db_conf['user'],
                             password=db_conf['password'], database=db_conf['database'])
        cursor = db.cursor()
        cursor.execute("SELECT TCFG_json FROM TCFG WHERE id=1")
    except Exception:
        traceback.print_exc()
        return None
    result = cursor.fetchone()
    li = json.loads(result[0])
    return jsonify(li)


app.run(debug=True, host='0.0.0.0', port=30855)
