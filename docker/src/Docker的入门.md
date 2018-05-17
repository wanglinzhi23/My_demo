### 安装centos7系统
图形界面安装的时候记得网卡部分一定要开启，否则无法分配ip

### centos7 系统上装Docker
1.检查
  uname -r 命令查看你当前的内核版本要大于 3.10
2.安装
  yum -y install docker-io
3.启动docker服务
  service docker start
4.镜像加速
  vi /etc/docker/daemon.json文件将文件内容替换为：
```
{
  "registry-mirrors": ["http://hub-mirror.c.163.com"]
}
```
5.重启docker
  systemctl restart docker
6.测试docker
  运行如下命令看到正确结果
  docker run hello-world
7.常用命令
	** 查看docker中的服务
	  docker ps -as --no-trunc查看docker中的服务，
	  "--no-trunc"是不截断输出。
```
[root@localdocker ~]# docker ps -as --no-trunc
CONTAINER ID                                                       IMAGE               COMMAND                               CREATED             STATUS              PORTS                      NAMES               SIZE
dc8d38b4550ad61852a2154d89be9025a60850a37f08a03639f8962afde5387a   redis:3.2           "docker-entrypoint.sh redis-server"   5 hours ago         Up 4 hours          0.0.0.0:6379->6379/tcp     redis6379           0 B (virtual 99.7 MB)
9b112f648fb811441bc87ddc63234f2a46261c016c58fc485795498f8647dc70   memcached:1.4       "docker-entrypoint.sh memcached"      5 hours ago         Up 4 hours          0.0.0.0:11211->11211/tcp   memcached11211      0 B (virtual 58.6 MB)
a130ad0d9094fe19fedef330bbd19b6e1a96733c29b2341b322ce9dd2d79852f   mysql:5.6.34        "docker-entrypoint.sh mysqld"         6 hours ago         Up 4 hours          0.0.0.0:3306->3306/tcp     mysql3306           2 B (virtual 327 MB)
```
	**= 删除某个服务
	  docker rm dc8  后面的dc8就是CONTAINER ID的前三位
	** 查看docker中的镜像
	  docker images
```
[root@localdocker ~]# docker images
REPOSITORY            TAG                 IMAGE ID            CREATED             SIZE
huiyanyun-api         latest              297a22f63b71        10 minutes ago      798 MB
docker.io/redis       3.2                 e97b1f10d81a        8 days ago          99.7 MB
docker.io/memcached   1.4                 bdb0ceca47d8        9 months ago        58.6 MB
docker.io/java        8                   d23bdf5b1b1b        15 months ago       643 MB
docker.io/java        latest              d23bdf5b1b1b        15 months ago       643 MB
docker.io/mysql       5.6.34              04c7801756fa        17 months ago       327 MB
```
	** 删除docker中的镜像
	  docker rmi 297 后面的297就是IMAGE ID的前三位


== 采用国内加速
注册得到一个url之后执行
curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://42752df5.m.daocloud.io
或者手动将 --registry-mirror 加入到你的 Docker 配置文件 /etc/docker/daemon.json 中。


== 服务器重启后如何开启由docker部署的服务
1. 服务器重启后，需要重新开启docker服务
  systemctl start docker
2. 查看全部container，包括exited的容器,找出redmine所对应的NAMES标签名称
```
[root@vm ~]# docker ps -as
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                     PORTS               NAMES                 SIZE
9096abb641cb        memcached:1.4       "docker-entrypoint..."   44 minutes ago      Exited (0) 5 minutes ago                       memcached11211        0 B (virtual 58.6 MB)
215d9dd93902        mysql:5.6.34        "docker-entrypoint..."   About an hour ago   Exited (0) 5 minutes ago                       mysql3306             0 B (virtual 327 MB)
3fb15a4be012        hello-world         "/hello"                 23 hours ago        Exited (0) 23 hours ago                        vibrant_nightingale   0 B (virtual 1.85 kB)
```
3. 启动服务
  docker start mysql3306
  或者
  docker start 215(id的至少前三位)
4.彻底解决
  在运行docker容器时可以加如下参数来保证每次docker服务重启后容器也自动重启：
  docker run --restart=always
  如果已经启动了则可以使用如下命令：
  docker update --restart=always <CONTAINER ID>


###Docker上安装 Mysql==

1.拉取镜像
  docker pull mysql:5.6.34
2.创建相关文件目录，如下
```
[root@vm mysql]# pwd
/usr/dockerfile/mysql
[root@vm mysql]# ll
total 0
drwxr-xr-x. 2 root root 6 May  9 05:29 conf
drwxr-xr-x. 2 root root 6 May  9 05:29 data
drwxr-xr-x. 2 root root 6 May  9 05:29 logs
```
3.运行命令安装
docker run -p 3306:3306 --name mysql3306 --restart=always -v /usr/dockerfile/mysql/conf:/etc/mysql/conf.d -v /usr/dockerfile/mysql/logs:/logs -v /usr/dockerfile/mysql/data:/mysql_data -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.6.34
4.Navicat数据库链接工具测试链接正常与否

###Docker上安装 memcached==

1.拉取镜像
  docker pull memcached:1.4
2.运行命令安装
docker run -p 11211:11211 --name memcached11211 --restart=always  -d memcached:1.4
3.检查
```
telnet 127.0.0.1 11211 //连接memcached
set foo 0 0 3                                                   保存命令
bar                                                             数据
STORED                                                          结果
get foo                                                         取得命令
VALUE foo 0 3                                                   数据
bar                                                             数据
END                                                             结束行
delete foo                                                      删除foo						
quit                                                            退出
```


###Docker上安装 redis==

1.拉取镜像
  docker pull  redis:3.2
2.创建目录来存储数据
  目录为: /usr/dockerfile/redis/data
3.运行命令安装
  docker run -p 6379:6379  --name redis6379 --restart=always  -d redis:3.2
3.检查
```
[root@vm dockerfile]# docker ps -as  //首先查看是否有服务运行
CONTAINER ID        IMAGE               COMMAND                  CREATED              STATUS              PORTS                      NAMES               SIZE
69f26f386fc9        redis:3.2           "docker-entrypoint..."   About a minute ago   Up About a minute   0.0.0.0:6379->6379/tcp     redis6379           0 B (virtual 99.7 MB)
[root@vm dockerfile]# docker exec -it 69f26f386fc9 redis-cli   //连接到redis中查看信息
127.0.0.1:6379> info
# Server
redis_version:3.2.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:6b048b7c45ebbb75
redis_mode:standalone
os:Linux 3.10.0-693.el7.x86_64 x86_64
arch_bits:64
multiplexing_api:epoll
gcc_version:4.9.2
process_id:1
run_id:f2c659133dd4872ae6ad3a7dfc7049a927a69671
tcp_port:6379
uptime_in_seconds:148
uptime_in_days:0
hz:10
lru_clock:15914219
executable:/data/redis-server
```