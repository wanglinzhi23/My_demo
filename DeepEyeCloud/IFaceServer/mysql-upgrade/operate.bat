
@echo off  
flyway %1 -configFile=./conf/flyway_dev.conf -baselineOnMigrate=true 

