# LogFlash
## Docker部署    
1. 下载[flink](https://www.apache.org/dyn/closer.lua/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz)，将flink程序包放在docker目录里
2. 创建logflash镜像
   ```bash
   cd docker
   ./build.sh --job-artifacts LogFlash-1.0-SNAPSHOT.jar --flink-path flink-1.10.0-bin-scala_2.11.tgz
   ```
3. 创建数据库镜像
    ```bash
   docker build -t logsql -f Dockerfile.mysql .
    ```
4. 创建file2Stream镜像
    ```bash
   docker build -t file2stream -f Dockerfile.file2stream .
    ```
5. 通过resources目录下的config.propeties中的sourceName参数指定数据源。使用file：将日志文件放到resources下，格式为/resources/日志类型名/raw/日志文件；使用socket：将日志文件放到data目录下，并在logFilePaths中写入日志路径，以/data开头
6. 修改model目录权限
    ```bash
   sudo chmod 777 models/ 
   ```
7. 启动容器
   ```bash
   FLINK_JOB=Entrance docker-compose up -d
   ```

