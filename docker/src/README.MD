#spring-boot工程发布docker示例工程
## 说明
  尝试着将spring-boot的工程自动发布到docker服务上
  
### 配置docker maven plugin并在properties中声明dockerHost
  <dockerHost>http://192.168.6.174:2375</dockerHost>
### 开启docker远程API操作端口2375；
  vi /usr/lib/systemd/system/docker.service
  将ExecStart这一行后面加上 -H tcp://0.0.0.0:2375 -H   unix:///var/run/docker.sock
```
[Service]
Type=notify
NotifyAccess=all
EnvironmentFile=-/run/containers/registries.conf
EnvironmentFile=-/etc/sysconfig/docker
EnvironmentFile=-/etc/sysconfig/docker-storage
EnvironmentFile=-/etc/sysconfig/docker-network
Environment=GOTRACEBACK=crash
Environment=DOCKER_HTTP_HOST_COMPAT=1
Environment=PATH=/usr/libexec/docker:/usr/bin:/usr/sbin
ExecStart=/usr/bin/dockerd-current \
		  -H tcp://0.0.0.0:2375 -H   unix:///var/run/docker.sock                 //新加入的一行
		  --add-runtime docker-runc=/usr/libexec/docker/docker-runc-current \
		  --default-runtime=docker-runc \
		  --exec-opt native.cgroupdriver=systemd \
		  --userland-proxy-path=/usr/libexec/docker/docker-proxy-current \
		  --seccomp-profile=/etc/docker/seccomp.json \
		  $OPTIONS \
		  $DOCKER_STORAGE_OPTIONS \
		  $DOCKER_NETWORK_OPTIONS \
		  $ADD_REGISTRY \
		  $BLOCK_REGISTRY \
		  $INSECURE_REGISTRY \
		  $REGISTRIES

```
### 重启docker服务
```
  systemctl daemon-reload
  systemctl restart docker
```
### 关闭centos7的系统防火墙
  `systemctl stop firewalld`
### 检查是否配置成功
  浏览器访问 http://192.168.6.174:2375/info看是否有正常信息
### 配置maven
```
	<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.3.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <!--指定远程docker的位置;开启远程docker的2375  -->
        <dockerHost>http://192.168.6.174:2375</dockerHost>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
	<build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
					 <!-- 这里是启动类的类全名 -->
                    <mainClass>org.lihao.demo.Application</mainClass>
                    <executable>true</executable>
                    <layout>JAR</layout>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- docker maven打包插件；可以将应用做成docker镜像 -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.12</version>
                <configuration>
                    <!-- 注意imageName一定要是符合正则[a-z0-9-_.]的，否则构建不会成功 -->
                    <!-- 详见：https://github.com/spotify/docker-maven-plugin    Invalid repository name ... only [a-z0-9-_.] are allowed-->
                    <imageName>springboot-demo</imageName>
                    <baseImage>java</baseImage>
                    <entryPoint>[ "sh", "-c", "java -Xms1024m -Xmx2048m -DisJar=false -Dspring.profiles.active='dev' -jar /${project.build.finalName}.jar" ]</entryPoint>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
### 运行命令打包上传
    运行`clean package docker:build -DskipTests=true`即可打包应用为docker镜像发布在远程服务器
### 其他
  1. 远程服务器可以根据镜像快速启动docker容器如使用daocloud
