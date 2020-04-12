#!/bin/bash
#if unspecified, the hostname of the container is taken as the JobManager address
FLINK_HOME=${FLINK_HOME:-"/opt/flink"}

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
    exec "$FLINK_HOME"/bin/taskmanager.sh start-foreground "$@"
  else
    exec "$FLINK_HOME"/bin/standalone-job.sh start-foreground "$@"
  fi
fi

exec "$@"
