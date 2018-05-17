该项目用来管理云天励飞数据库变更，请停止一切直接对数据库schema的直接修改，使用增加脚本来维护数据库，从而做到数据库的自动更新。

1. 安装flywaydb:
https://flywaydb.org/documentation/commandline/
并设置相应的环境变量

2， 运行如：
linux环境运行：
sh evolution.sh test
其中test 是conf flway_test.conf 中的test

windows运行
1 将operate.bat和upgrade.bat copy到与conf文件夹同目录
双击upgrade.bat

使用windows可以直接运行：flyway migrate -configFile=./conf/flyway_test.conf -baselineOnMigrate=true

运行如果报错flyway repair -configFile=./conf/flyway_test.conf -baselineOnMigrate=true，可以将schema_version表错误记录删除

3, 开发详情请参考：
https://flywaydb.org/documentation/migration/sql#syntax