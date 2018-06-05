#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH
#获取配置文件名称
confName=`pwd|cut -d / -f 5`

nohup java -XX:+UseConcMarkSweepGC -Xmx2048m -Xms1024m  -XX:+PrintGCDateStamps -XX:+PrintGCDetails  -Xloggc:./logs/java_gc.log  -XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/ -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=18980 -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar api.jar --spring.profiles.active=${confName} --isJar=true > nohup.out 2>&1 &
#nohup java -XX:+UseConcMarkSweepGC -Xmx2048m -Xms1024m  -XX:+PrintGCDateStamps -XX:+PrintGCDetails  -Xloggc:./logs/java_gc.log  -XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/ -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8  -jar api.jar --spring.profiles.active=ifaas --isJar=true > /dev/null 2>&1 &
