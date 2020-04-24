-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: localhost    Database: anomalies
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `anomaly_log`
--

DROP SCHEMA IF EXISTS anomalies;
CREATE SCHEMA anomalies;
USE anomalies;
DROP TABLE IF EXISTS `anomaly_log`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anomaly_log`
(
    `id`               int(11)      NOT NULL AUTO_INCREMENT,
    `time`             varchar(100) NOT NULL,
    `unixtime`         varchar(15)  NOT NULL,
    `level`            varchar(20)   DEFAULT NULL,
    `component`        varchar(500)  DEFAULT NULL,
    `content`          varchar(3000) DEFAULT NULL,
    `template`         varchar(3000) DEFAULT NULL,
    `paramlist`        varchar(3000) DEFAULT NULL,
    `eventid`          varchar(200)  DEFAULT NULL,
    `anomalylogs`      text,
    `anomalyrequest`   text,
    `anomalywindow`    varchar(200)  DEFAULT NULL,
    `anomalytype`      varchar(10)   DEFAULT NULL,
    `anomalytemplates` varchar(500)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 21
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anomaly_log`
--

LOCK TABLES `anomaly_log` WRITE;
/*!40000 ALTER TABLE `anomaly_log`
    DISABLE KEYS */;
INSERT INTO `anomaly_log`
VALUES (1, '03:17:43:252', '11863252', 'ERROR', '1',
        '####### Error occurred, reason is [This view isn\'t initialized.]',
        '####### Error occurred, reason is [This view isn\'t initialized.]', '\"\"', 'a66a46c7',
        '%%%%% Servcice [1] completed its judging job.\n%%%%% Check to see if service [1] needs switching model...\n##### Totally selected [22] glass(es) from db for judging.\n##### CSV file path of glass [T6K495R031B] is [/mnt/IMG/ARRAY/LINK/TCAOH//T6K49/T6K495R0/T6K495R031B/18350_T6K495R031B_20190604_025941.csv]\n##### ReadDefectFilePath and WriteDefectFilePath of glass [3234183] is [/mnt/IMG/ARRAY/LINK/TCAOH//TC62231AAG00/T6K49/T6K495R0/T6K495R031B/18350_T6K495R031B_20190604_025941.csv] and [/mnt/IMG/ARRAY/LINK/TCAOH//TC62231AAG00/T6K49/T6K495R0/T6K495R031B/1835B_T6K495R031B_20190604_025941.csv]\n##### CSV directory of glass [T6K495R032B] is [/mnt/IMG/ARRAY/LINK/TCAOH//T6K49/T6K495R0/T6K495R032B/18350_T6K495R032B]\n####### Error occurred, reason is [This view isn\'t initialized.]\n',
        '####### Error occurred, reason is [This view isn\'t initialized.]\n', '', 'Sequence', 'a66a46c7\n'),
       (2, '04:24:03:333', '15843333', 'INFO', '1',
        '===> handleFeedResponse# Cache cleaning of glass [3235041] started...',
        '===> handleFeedResponse# Cache cleaning of glass <*> <*>', '\"[[3235041] started...]\"', '5a66b200',
        'Received ADCProcessEndReply message.\n*Notify the lock of view [1011] on service [1]*\n***********************************\n===> handleFeedResponse# Cache cleaning of glass [3235041] started...\n',
        '===> handleFeedResponse# Cache cleaning of glass [3235041] started...\n', '', 'Sequence', '5a66b200\n'),
       (3, '04:41:16:680', '16876680', 'ERROR', '1',
        '####### Error occurred, reason is [This view isn\'t initialized.]',
        '####### Error occurred, reason is [This view isn\'t initialized.]', '\"\"', 'a66a46c7',
        '%%%%% Submit new judging job on servcie [1] with model [12850_61901_Top15_Y190423$20190423103100463]\n##### Exiting from glass selector on service [2].\n##### Totally selected [24] glass(es) from db for judging.\n%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n##### CSV directory of glass [T6B495V03UB] is [/mnt/IMG/ARRAY/LINK/TCAOH//T6B49/T6B495V0/T6B495V03UB/12850_T6B495V03UB]\n##### CSV file path of glass [T6B495V03UB] is [/mnt/IMG/ARRAY/LINK/TCAOH//T6B49/T6B495V0/T6B495V03UB/12850_T6B495V03UB_20190604_044033.csv]\n##### ReadDefectFilePath and WriteDefectFilePath of glass [3235204] is [/mnt/IMG/ARRAY/LINK/TCAOH//TC61901AAG00/T6B49/T6B495V0/T6B495V03UB/12850_T6B495V03UB_20190604_044033.csv] and [/mnt/IMG/ARRAY/LINK/TCAOH//TC61901AAG00/T6B49/T6B495V0/T6B495V03UB/1285B_T6B495V03UB_20190604_044033.csv]\n##### Submit judging job for glass [3235181]\n####### Successfully sent feed message for glass [3235181] on service [1]\n######## Waiting for judging result of glass [3235181]\n*Notify the lock of view [1011] on service [1]*\n***********************************\n===># Glass [3235143] using view [3011] on service [3] is in progress:{\"percent\":\"0.783333\"}\n####### Error occurred, reason is [This view isn\'t initialized.]\n',
        '##### Submit judging job for glass [3235181]\n######## Waiting for judging result of glass [3235181]\n####### Error occurred, reason is [This view isn\'t initialized.]\n',
        '', 'Sequence', 'f5ffd670\n3147b85c\na66a46c7\n'),
       (4, '08:57:41:978', '32261978', 'INFO', '1', '##### Totally selected [28] glass(es) from db for judging.',
        '##### Totally selected <*> glass(es) from db for judging.', '\"[[28]]\"', '8b0bd2d5',
        '*Notify the lock of view [3011] on service [3]*\n***********************************\n##### Totally selected [28] glass(es) from db for judging.\n',
        '##### Totally selected [28] glass(es) from db for judging.\n', '', 'Sequence', '8b0bd2d5\n'),
       (5, '10:16:41:286', '37001286', 'ERROR', '1',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING', '\"\"',
        'b61ae5f3',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n*Notify the lock of view [1011] on service [1]*\n***********************************\njava.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING\n',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING\n', '',
        'Sequence', 'b61ae5f3\n'),
       (6, '10:17:41:228', '37061228', 'ERROR', '1',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in <*> seconds.', '\"[60]\"', '852ba4e4',
        '===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.675\"}\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\n',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\n', '', 'Sequence',
        '852ba4e4\n'),
       (7, '10:17:41:228', '37061228', 'ERROR', '1', 'Getting ready to send email', 'Getting ready to send email',
        '\"\"', 'cdde3406',
        '===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.675\"}\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n',
        'Getting ready to send email\n', '', 'Sequence', 'cdde3406\n'),
       (8, '10:17:41:229', '37061229', 'ERROR', '1', '*************************************************',
        '*************************************************', '\"\"', 'df6da263',
        '===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.675\"}\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\n',
        '*************************************************\n', '', 'Sequence', 'df6da263\n'),
       (9, '10:17:41:229', '37061229', 'ERROR', '1',
        'Getting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_2] with view [1012] and model [15850_62231_HBT_Top20_Y190214].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1012] of service.]',
        'Getting ready for mail with content [Execution of sending <*> command failed while using device <*> to do <*> on service [<*> <*> Judge <*>] with view <*> and model <*> <*> HBT <*> <*> caused by no response return from device in <*> seconds.<br>Current view status of device <*> is <*>.<br>Here are some tips for solving this problem:<br>	 <*>.To check the availability of network.<br>	 <*>.To check the existence of device <*>.<br>	 <*>.To check the availability of view <*> of service.]',
        '\"[]\"', '7f9cb7d1',
        '===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.675\"}\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_2] with view [1012] and model [15850_62231_HBT_Top20_Y190214].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1012] of service.]\n',
        'Getting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_2] with view [1012] and model [15850_62231_HBT_Top20_Y190214].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1012] of service.]\n',
        '', 'Sequence', '7f9cb7d1\n'),
       (10, '10:17:41:234', '37061234', 'ERROR', '1',
        'view [1012] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable',
        'view <*> on device <*> is unavailable', '\"[[1012], [1273a72084c7ee4a1a2559ac5bf00792]]\"', 'c6a22991',
        '===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.675\"}\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_2] with view [1012] and model [15850_62231_HBT_Top20_Y190214].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1012] of service.]\nview [1012] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n',
        'view [1012] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n', '', 'Sequence', 'c6a22991\n'),
       (11, '10:17:44:079', '37064079', 'INFO', '1', 'javax.mail.internet.MimeMessage@776469b1',
        'javax.mail.internet.MimeMessage<*>', '\"[@776469b1]\"', '2855f8e2',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_2] with view [1012] and model [15850_62231_HBT_Top20_Y190214].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1012] of service.]\nview [1012] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n*Notify the lock of view [3011] on service [3]*\n***********************************\n##### Exiting from glass selector on service [3].\n%%%%% Servcice [3] completed its judging job.\n%%%%% Check to see if service [3] needs switching model...\n===># Glass [3238518] using view [1012] on service [2] is in progress:{\"percent\":\"0.8875\"}\njavax.mail.internet.MimeMessage@776469b1\n',
        'javax.mail.internet.MimeMessage@776469b1\n', '', 'Sequence', '2855f8e2\n'),
       (12, '11:37:45:539', '41865539', 'ERROR', '1',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING', '\"\"',
        'b61ae5f3',
        '%%%%% Servcice [1] completed its judging job.\n%%%%% Check to see if service [1] needs switching model...\njava.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING\n',
        'java.lang.IllegalArgumentException: No enum constant com.delta.adc.diva.utils.DivaStatus.DESTORYING\n', '',
        'Sequence', 'b61ae5f3\n'),
       (13, '11:38:45:402', '41925402', 'ERROR', '1', '*************************************************',
        '*************************************************', '\"\"', 'df6da263',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\n*************************************************\n',
        '*************************************************\n', '', 'Sequence', 'df6da263\n'),
       (14, '11:38:45:402', '41925402', 'ERROR', '1',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in <*> seconds.', '\"[60]\"', '852ba4e4',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\n',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\n', '', 'Sequence',
        '852ba4e4\n'),
       (15, '11:38:45:402', '41925402', 'ERROR', '1', 'Getting ready to send email', 'Getting ready to send email',
        '\"\"', 'cdde3406',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n',
        'Getting ready to send email\n', '', 'Sequence', 'cdde3406\n'),
       (16, '11:38:45:402', '41925402', 'ERROR', '1', '*************************************************',
        '*************************************************', '\"\"', 'df6da263',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\n*************************************************\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\n',
        '*************************************************\n', '', 'Sequence', 'df6da263\n'),
       (17, '11:38:45:403', '41925403', 'ERROR', '1',
        'Getting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_1] with view [1011] and model [1D850_65901_HBT_Top2_Y190508].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1011] of service.]',
        'Getting ready for mail with content [Execution of sending <*> command failed while using device <*> to do <*> on service [<*> <*> Judge <*>] with view <*> and model <*> <*> HBT <*> <*> caused by no response return from device in <*> seconds.<br>Current view status of device <*> is <*>.<br>Here are some tips for solving this problem:<br>	 <*>.To check the availability of network.<br>	 <*>.To check the existence of device <*>.<br>	 <*>.To check the availability of view <*> of service.]',
        '\"[]\"', '7f9cb7d1',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_1] with view [1011] and model [1D850_65901_HBT_Top2_Y190508].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1011] of service.]\n',
        'Getting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_1] with view [1011] and model [1D850_65901_HBT_Top2_Y190508].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1011] of service.]\n',
        '', 'Sequence', '7f9cb7d1\n'),
       (18, '11:38:45:408', '41925408', 'ERROR', '1',
        'view [1011] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable',
        'view <*> on device <*> is unavailable', '\"[[1011], [1273a72084c7ee4a1a2559ac5bf00792]]\"', 'c6a22991',
        '%%%%% Servcice [2] completed its judging job.\n%%%%% Check to see if service [2] needs switching model...\n%%%%% Submit new judging job on servcie [2] with model [18850_62201_aobao_190117$20190117211425226]\n##### Exiting from glass selector on service [3].\nCan not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_1] with view [1011] and model [1D850_65901_HBT_Top2_Y190508].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1011] of service.]\nview [1011] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n',
        'view [1011] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n', '', 'Sequence', 'c6a22991\n'),
       (19, '11:38:48:532', '41928532', 'INFO', '1', 'javax.mail.internet.MimeMessage@743b1032',
        'javax.mail.internet.MimeMessage<*>', '\"[@743b1032]\"', '2855f8e2',
        'Can not get response from device 1273a72084c7ee4a1a2559ac5bf00792 in 60 seconds.\nGetting ready to send email\n*************************************************\nGetting ready for mail with content [Execution of sending [status] command failed while using device [1273a72084c7ee4a1a2559ac5bf00792] to do [Judging] on service [141_1_Judge_1] with view [1011] and model [1D850_65901_HBT_Top2_Y190508].<br>It\'s caused by no response return from device in [60] seconds.<br>Current view status of device [1273a72084c7ee4a1a2559ac5bf00792] is [unknown].<br>Here are some tips for solving this problem:<br>	 1.To check the availability of network.<br>	 2.To check the existence of device [1273a72084c7ee4a1a2559ac5bf00792].<br>	 3.To check the availability of view [1011] of service.]\nview [1011] on device [1273a72084c7ee4a1a2559ac5bf00792] is unavailable\n*Notify the lock of view [3011] on service [3]*\n***********************************\n%%%%% Servcice [3] completed its judging job.\n%%%%% Check to see if service [3] needs switching model...\n%%%%% Submit new judging job on servcie [3] with model [1E850_62221_TOP2_1223$20181223123938223]\njavax.mail.internet.MimeMessage@743b1032\n',
        'javax.mail.internet.MimeMessage@743b1032\n', '', 'Sequence', '2855f8e2\n'),
       (20, '14:26:26:174', '51986174', 'INFO', '1',
        '===> handleFeedResponse# Cache cleaning of glass [3240954] started...',
        '===> handleFeedResponse# Cache cleaning of glass <*> <*>', '\"[[3240954] started...]\"', '5a66b200',
        '======> Reading raw data from file [/mnt/IMG/ARRAY/LINK/TCAOH//T6K49/T6K495T0/T6K495T0S9B/18250_T6K495T0S9B_20190604_141355.csv] and Generating new file [/mnt/IMG/ARRAY/TCADC/TC62231AAG00/T6K49/T6K495T0/T6K495T0S9B/Source/1825B_T6K495T0S9B_20190604_142621.csv]\nReceived ADCProcessEndReply message.\n*Notify the lock of view [3011] on service [3]*\n***********************************\n%%%%% Servcice [3] completed its judging job.\n%%%%% Check to see if service [3] needs switching model...\n%%%%% Submit new judging job on servcie [3] with model [12850_62231_HBT_TOP20_0216$20190218105406782]\n===> handleFeedResponse# Cache cleaning of glass [3240954] started...\n',
        '===> handleFeedResponse# Cache cleaning of glass [3240954] started...\n', '', 'Sequence', '5a66b200\n');
/*!40000 ALTER TABLE `anomaly_log`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2019-11-20 17:11:04
