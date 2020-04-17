# LogFlash
## Docker部署
### logflash_web部署
1. 下载前端和数据库项目
    ```bash
    git clone https://e.coding.net/Midor/LogFlash.git
    cd LogFlash
    ```
2. 创建mysql镜像
    ```bash
    docker build -t logsql -f Dockerfile.mysql .
    ```
3. 创建web镜像
    ```bash
    docker build -t logflash_web -f Dockerfile.logflash .
    ```
### logflash部署
1. 修改路径
    
    将代码中的路径`src/main/resources`修改为`/opt/resources`
    
2. maven打包
    
    使用maven里的package命令打包，将jar包放在docker目录里
    
3. 下载[flink](https://www.apache.org/dyn/closer.lua/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz)，将flink程序包放在docker目录里
4. 创建logflash镜像
   ```bash
   cd docker
   ./build.sh --job-artifacts LogFlash-1.0-SNAPSHOT.jar --flink-path flink-1.10.0-bin-scala_2.11.tgz
   ```
5. 启动容器
   ```bash
   FLINK_JOB=Entrance docker-compose up -d
   ```

