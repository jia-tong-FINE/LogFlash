# LogFlash

## Docker部署
1. 修改路径
    将代码中所有的`src/main/resources`修改为`/opt/resources`
    
    将配置文件中`shareMemoryFilePath = src/main/resources/models`修改为`shareMemoryFilePath = /opt/models`
2. maven打包
    使用maven里的package打包
3. 下载[flink](https://www.apache.org/dyn/closer.lua/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz)
4. 将build.sh, Dockerfile, docker-compose.yml, docker-entrypoint.sh, jar包, flink安装包, resources (包括数据和配置文件) 置于docker目录下
5. 创建镜像
   ```bash
   ./build.sh --job-artifacts LogFlash-1.0-SNAPSHOT.jar --flink-path flink-1.10.0-bin-scala_2.11.tgz
   ```
6. 启动容器
   ```bash
   FLINK_JOB=workflow.WorkFlowMode2 FLINK_JOB_ARGUMENTS="--log-path <PATH_TO_LOG>" docker-compose up -d
   ```

