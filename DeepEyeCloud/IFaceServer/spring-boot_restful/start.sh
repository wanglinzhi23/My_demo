#!/bin/bash
#: Title : IFaceServer.start.sh -8082 true
#: Date : 2016-01-07
#: Author : "Knight.Zhou" <youngwelle@gmail.com>
#: Version : 1.0
#: Description : start IFaceServer!
#: Options : None
#: Example : java -jar api.jar --spring.profiles.active=8082 --isJar=true &
#: start it

#$1 [dev, test, lg01,deploy,local]
#flyway migrate -configFile=./conf/flyway_$1.conf -baselineOnMigrate=true

echo "Starting..."

nohup java -XX:+UseConcMarkSweepGC -Xmx2048m -Xms1024m  -XX:+PrintGCDateStamps -XX:+PrintGCDetails  -Xloggc:./logs/java_gc.log  -XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/ -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false  -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar api.jar --spring.profiles.active=$1 --isJar=$2 &
