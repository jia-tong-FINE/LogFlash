#!/bin/bash
usage() {
  cat <<EOF
Usage:
  build.sh --job-artifacts <comma-separated-paths-to-job-artifacts> --flink-path <path-to-flink> [--image-name <image>]
  build.sh --help
  If the --image-name flag is not used the built image name will be 'flink-job'.
EOF
  exit 1
}

while [[ $# -ge 1 ]]; do
  key="$1"
  case $key in
  --job-artifacts)
    JOB_ARTIFACTS_PATH="$2"
    shift
    ;;
  --flink-path)
    FLINK_PATH="$2"
    shift
    ;;
  --image-name)
    IMAGE_NAME="$2"
    shift
    ;;
  --help)
    usage
    ;;
  *)
    # unknown option
    ;;
  esac
  shift
done

IMAGE_NAME=${IMAGE_NAME:-flink-job}

# TMPDIR must be contained within the working directory so it is part of the
# Docker context. (i.e. it can't be mktemp'd in /tmp)
TMPDIR=_TMP_

cleanup() {
  rm -rf "${TMPDIR}"
}
trap cleanup EXIT
mkdir -p "${TMPDIR}"

JOB_ARTIFACTS_DIST="${TMPDIR}/artifacts"
mkdir -p ${JOB_ARTIFACTS_DIST}

job_artifacts_array=("${JOB_ARTIFACTS_PATH}")
for artifact in "${job_artifacts_array[@]}"; do
  cp "${artifact}" ${JOB_ARTIFACTS_DIST}/
done

if [ -n "${FLINK_PATH}" ]; then
  FLINK_DIST="${TMPDIR}/flink.tgz"
  cp "${FLINK_PATH}" "${FLINK_DIST}"
else
  usage
fi

docker build --build-arg flink_dist="${FLINK_DIST}" --build-arg job_artifacts="${JOB_ARTIFACTS_DIST}" -t "${IMAGE_NAME}" .
