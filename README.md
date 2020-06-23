# LogFlash

## Docker部署
*请先安装docker、docker-compose、node再进行如下步骤。*

1. 下载flink
    ```bash
   cd docker && wget https://mirrors.tuna.tsinghua.edu.cn/apache/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz
   ```

2. 创建logflash镜像
   ```bash
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
   
5. 通过resources/config.propeties中的sourceName指定数据输入形式
    - file：将日志文件放到resources目录下，格式为resources/<日志类型名>/raw/<日志文件>
    - socket：将日志文件放到data目录下，并在logFilePaths中写入日志路径，以/data开头
   
6. 创建web后端服务器镜像
    - web服务器使用单独的配置文件连接数据库，创建镜像前，如果修改了MySQL用户名与密码，则要编辑db-server.json配置文件中的用户名与密码与config.properties中一致
    
    ```bash
    docker build -t dbserver -f Dockerfile.dbserver .
    ```
    
7. 启动web前端
   ```bash
   # 安装依赖
   cd LogFlash_web && npm install --registry=https://registry.npm.taobao.org
   # 启动服务
   nohup npm run dev >web.log 2>&1 &
   ```
   
8. 启动容器
   ```bash
   cd .. && FLINK_JOB=Entrance docker-compose up -d
   ```
## 配置参数说明
1. 日志数据输入配置参数
```
sourceName=file or socket  #日志数据输入形式（文件或socket）
socketHost  #socket服务器地址
socketPort  #socket服务器端口号
```
2. 日志模板挖掘参数
```
logFormat   #日志格式，支持多种格式匹配，以@符号分隔不同日志格式
示例：logFormat=[<Component>][<Level>] <Date> <Time>: <Content>@<TraceId> <Process> <Date> <Time> <Level> <Component>: <Content>
timeFormat  #时间戳格式，支持多种格式匹配，以@符号分隔不同时间戳格式
示例：timeFormat = HH:mm:ss,SSS@HH:mm:ss,SSS
regex   #日志变量拆分正则
示例：regex=@[a-z0-9]+$&\\[[A-Za-z0-9\\-\\/]+\\]
```
3.TCFG故障诊断模型训练与推断参数 
```
slidingWindowSize   #滑动时间窗口大小（ms）
slidingWindowStep   #窗口滑动步长（ms）
maxOutOfOrderness   #最大乱序偏差时间限制（ms）
gamma   #梯度更新步长
gradLimitation  #单次最大参数更新限制
delta   #日志间最小时间差限制（ms）
beta    #衰变率
alpha   #参数初始值
TCFGWriteInterval   #共享内存TCFG更新频率（ms）
TCFGReadInterval    #异常检测模型更新TCFG频率（ms）
matrixWriteInterval #共享内存转移参数矩阵更新频率（ms）
```
4. 共享内存控制参数
```
共享内存切分（共40mb）：
#############################
#      TrainingFlag(1)      #
#      DetectionFlag(1)     #
#        TCFGRegion         #
# TransferParamMatrixRegion #
#       TuningRegion        #
#   TemplateUpdateRegion    #
#      ParseTreeRegion      #
#############################
shareMemoryFilePath #共享内存通道文件路径
transferParamMatrixSize #转移参数矩阵所占内存大小（byte）
TCFGSize    #TCFG故障诊断模型所占内存大小（byte）
tuningRegionSize    #待修正子图缓存大小（byte）
templateUpdateRegionSize    #日志模板更新信息所占内存大小（byte）
parseTreeRegionSize #日志模板树所占内存大小（byte）
```
5. 人工反馈机制参数
```
suspiciousTimeForLatencyAnomaly #延迟异常存疑观测时间（ms）
suspiciousTimeForSequenceAnomaly    #序列异常存疑观测时间（ms）
suspiciousTimeForRedundancyAnomaly  #冗余异常存疑观测时间（ms）
falseAlarmsProcessingInterval    #人工反馈处理频率（ms）
```
6. mysql数据库配置参数
```
database    #数据库名
databaseUrl #数据库URL
mysqlUser   #用户名
mysqlPassword   #密码
```

