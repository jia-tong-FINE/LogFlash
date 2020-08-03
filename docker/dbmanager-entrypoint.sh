#!/bin/sh
sh /wait-for.sh mysql:3306
java -jar /opt/logflashdb.jar