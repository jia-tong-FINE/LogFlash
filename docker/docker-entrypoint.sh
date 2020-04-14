#!/bin/bash
#if unspecified, the hostname of the container is taken as the JobManager address
FLINK_HOME=${FLINK_HOME:-"/opt/flink"}
CONF_FILE="${FLINK_HOME}/conf/flink-conf.yaml"
LOG4J_FILE="${FLINK_HOME}/conf/log4j-console.properties"
JOB_CLUSTER="job-manager"
TASK_MANAGER="task-manager"

CMD="$1"
shift

if [ "${CMD}" == "--help" ] || [ "${CMD}" == "-h" ]; then
  echo "Usage: $(basename "$0") (${JOB_CLUSTER}|${TASK_MANAGER})"
  exit 0
elif [ "${CMD}" == "${JOB_CLUSTER}" ] || [ "${CMD}" == "${TASK_MANAGER}" ]; then
  echo "Starting the ${CMD}"

  if [ "${CMD}" == "${TASK_MANAGER}" ]; then
    sed -i -e "s/log4j.rootLogger=INFO, console/log4j.rootLogger=INFO, console, file/g" ${LOG4J_FILE}
    echo -e "# Log all infos in the given file\nlog4j.appender.file=org.apache.log4j.FileAppender\nlog4j.appender.file.file=${FLINK_HOME}/log/output.log\nlog4j.appender.file.append=false\nlog4j.appender.file.layout=org.apache.log4j.PatternLayout\nlog4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %-60c %x - %m%n" >> ${LOG4J_FILE}
    echo -e "web.log.path: /opt/flink/log/output.log\ntaskmanager.log.path: /opt/flink/log/output.log" >> ${CONF_FILE}
    exec "$FLINK_HOME"/bin/taskmanager.sh start-foreground "$@"
  else
    sed -i -e "s/log4j.rootLogger=INFO, console/log4j.rootLogger=INFO, console, file/g" ${LOG4J_FILE}
    echo -e "# Log all infos in the given file\nlog4j.appender.file=org.apache.log4j.FileAppender\nlog4j.appender.file.file=${FLINK_HOME}/log/output.log\nlog4j.appender.file.append=false\nlog4j.appender.file.layout=org.apache.log4j.PatternLayout\nlog4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %-60c %x - %m%n" >> ${LOG4J_FILE}
    echo -e "web.log.path: /opt/flink/log/output.log\ntaskmanager.log.path: /opt/flink/log/output.log" >> ${CONF_FILE}
    exec "$FLINK_HOME"/bin/standalone-job.sh start-foreground "$@"
  fi
fi

exec "$@"
