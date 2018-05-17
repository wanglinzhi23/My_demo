## 基础业务插件化方案集之方案1: 赋能方案 示例说明

由Spring Boot 系列的软件设计理念为依据，设计可插拔式服务组合示例，能够方便的进行集成

特点：
- 实现代码级别的版本管理，而非服务级别
- 适用于快速集成
- 提供初始化SQL脚本
- 提供可选的默认的UI操作页面
- 依托于SpringIOC容器，业务代码可直接注入公共模块提供的Bean

### FAQ
#### 服务级的组件 or 代码级的组件？
此方案采用代码级组件方式，将组件代码作为我们的产品进行维护

#### 数据库脚本如何管理
依赖于flyway进行管理，将服务自身的migrate放到默认文件夹中，服务启动时会自动运行。同时业务项目的代码使用flyway也不会冲突`可参考示例2`

#### 为什么会在demo-core里放controller？
为了demo-ui而使用，如果不需要提供默认的操作页面建议不要写controller到公共模块里

#### 为什么想到这个方案？
使用swagger时发现这种通过一个注解为服务赋能的方式比较方便，而且Spring Boot系列的设计思想也是组件化灵活配置。所以参照这种设计思想进行此设计。


### 使用方法
**pom.xml**
```xml
<project>
    <dependencies>
        <!-- 引入核心依赖 -->
            <dependency>
                <groupId>org.lihao</groupId>
                <artifactId>demo-core</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
            <!-- 默认的UI页面可以不引入 -->
            <dependency>
                <groupId>org.lihao</groupId>
                <artifactId>demo-ui</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
    </dependencies>
    <!-- 设置Maven私服的仓库地址 -->
    <repositories>
        <repository>
            <id>Central</id>
            <url>http://192.168.2.34:8081/nexus/content/groups/public/</url>
        </repository>
    </repositories>
</project>

```


**Application.java**
在启动类上添加`EnableDemoCore`注解启用模块
```java
@SpringBootApplication
@EnableDemoCore
public class SampleAnnotationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleAnnotationApplication.class, args);
	}

}
```

**application.properties**
```properties
##==== 配置数据源，flyway会自动初始化组件所需要的表结构 ====##
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/intellif_demo
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=

##==== flyway ====##
spring.flyway.locations=classpath:db/migration
spring.flyway.out-of-order=true
spring.flyway.ignore-missing-migrations=true

##==== mybatis通用配置 ====##
mybatis.type-aliases-package=org.lihao.demo.core.entity
mybatis.mapper-locations=classpath*:mybatis/mapper/*.xml
```

#### 示例
  - [示例1: demo-proj(超简)](http://192.168.90.8/lihao/demo1/tree/master/demo-proj)
  - [示例2: demo-app](http://192.168.90.8/lihao/demo1/tree/master/demo-app)

### 链接
  - [demo-core 源码](http://192.168.90.8/lihao/demo1/tree/master/demo-core)
  - [demo-ui 源码](http://192.168.90.8/lihao/demo1/tree/master/demo-ui)

