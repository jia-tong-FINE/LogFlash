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
   docker build -t logsql -f Dockerfile.mysql
    ```
4. 启动容器
   ```bash
   FLINK_JOB=Entrance docker-compose up -d
   ```

