#!/bin/bash
#$1 [dev, test, lg01,deploy,local]
flyway repair  -configFile=./conf/flyway_$1.conf -baselineOnMigrate=true
flyway migrate -configFile=./conf/flyway_$1.conf -baselineOnMigrate=true
