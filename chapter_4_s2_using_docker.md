# Chapter 4: 测试关系型数据库 - 使用Docker创建临时数据库

在[Chapter 4: 测试关系型数据库 - 基本做法][chapter_4_s1_basic]里我们使用的是H2数据库，这是为了让你免去你去安装/配置一个数据库的工作，能够尽快的了解到集成测试的过程。

在文章里也说了：

> 在真实的开发环境中，集成测试用数据库应该和最终的生产数据库保持一致

那么很容易就能想到两种解决方案：

1. 开发团队使用共用同一个数据库。这样做的问题在于：当有多个集成测试同时在跑时，会产生错误的测试结果。
2. 每个人使用自己的数据库。这样做的问题在于让开发人员维护MySQL数据库挺麻烦的。

那么做到能否这样呢？

1. 测试启动前，创建一个MySQL数据库
2. 测试过程中连接到这个数据库
3. 测试结束后，删除这个MySQL数据库

So, Docker comes to the rescue。

我们还是会以[Chapter 4: 测试关系型数据库 - 基本做法][chapter_4_s1_basic]里的`FooRepositoryImpl`来做集成测试（[代码在这里][code-rdbs-docker]）。下面来讲解具体步骤：

## 安装Docker

请查阅官方文档。并且掌握Docker的基本概念。

## 配置properties

先定义几个properties：

```xml
<properties>
  <fabric8.dmp.version>0.28.0</fabric8.dmp.version>
  <maven-surefire-plugin.version>2.22.0</maven-surefire-plugin.version>
  <maven-failsafe-plugin.version>2.19</maven-failsafe-plugin.version>
  <!-- 跳过所有测试的flag -->
  <skipTests>false</skipTests>
  <!-- 跳过集成测试的flag -->
  <skipITs>${skipTests}</skipITs>
  <!-- 跳过单元测试的flag -->
  <skipUTs>${skipTests}</skipUTs>
</properties>
```

## 配置fabric8 docker-maven-plugin

[farbic8 docker-maven-plugin][fabric8-dmp]顾名思义就是一个能够使用docker的maven plugin。它主要功能有二：

1. [创建Docker image][fabric8-dmp-build]
2. [启动Docker container][fabric8-dmp-start]

我们这里使用**启动Docker container**的功能。

大致配置如下

```xml
 <plugin>
   <groupId>io.fabric8</groupId>
   <artifactId>docker-maven-plugin</artifactId>
   <version>${fabric8.dmp.version}</version>

   <configuration>
     <!--  当skipITs的时候，跳过 -->
     <skip>${skipITs}</skip>
     <images>
       <image>
         <!-- 使用mysql:8 docker image -->
         <name>mysql:8</name>
         <!-- 定义docker run mysql:8 时的参数 -->
         <run>
           <ports>
             <!-- host port到container port的映射
             这里随机选择一个host port，并将值存到property docker-mysql.port里 -->
             <port>docker-mysql.port:3306</port>
           </ports>
           <!-- 启动时给的环境变量，参阅文档：https://hub.docker.com/_/mysql -->
           <env>
             <MYSQL_ROOT_PASSWORD>123456</MYSQL_ROOT_PASSWORD>
             <MYSQL_DATABASE>test</MYSQL_DATABASE>
             <MYSQL_USER>foo</MYSQL_USER>
             <MYSQL_PASSWORD>bar</MYSQL_PASSWORD>
           </env>
           <!-- 设置判定container启动成功的的条件及timeout -->
           <wait>
             <!-- 如果container打出了这行日志，则说明容器启动成功 -->
             <log>MySQL init process done. Ready for start up.</log>
             <time>120000</time>
           </wait>
         </run>
       </image>
     </images>
   </configuration>

   <executions>
     <execution>
       <!-- 在集成测试开始前启动容器 -->
       <id>start</id>
       <phase>pre-integration-test</phase>
       <goals>
         <goal>start</goal>
       </goals>
     </execution>
     <execution>
       <!-- 在集成测试结束后停止并删除容器 -->
       <id>stop</id>
       <phase>post-integration-test</phase>
       <goals>
         <goal>stop</goal>
       </goals>
     </execution>
   </executions>
 </plugin>
```

## 配置maven-failsafe-plugin

maven-failsafe-plugin用来执行集成测试`*IT.java`。

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>${maven-failsafe-plugin.version}</version>
  <executions>
    <execution>
      <id>integration-test</id>
      <goals>
        <goal>integration-test</goal>
      </goals>
    </execution>
    <execution>
      <id>verify</id>
      <goals>
        <goal>verify</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <!-- 当skipTests的时候, 跳过 -->
    <skipTests>${skipTests}</skipTests>
    <!-- 当skipITs的时候, 跳过 -->
    <skipITs>${skipITs}</skipITs>
    <!-- 我们被测的是一个Spring Boot项目，因此可以通过System Properties把MySQL container的相关信息传递给程序
    详见文档：https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/html/boot-features-external-config.html
    -->
    <systemPropertyVariables>
      <spring.datasource.url>jdbc:mysql://localhost:${docker-mysql.port}/test</spring.datasource.url>
      <spring.datasource.username>foo</spring.datasource.username>
      <spring.datasource.password>bar</spring.datasource.password>
    </systemPropertyVariables>
  </configuration>
</plugin>
```

## 配置maven-surefire-plugin

maven-surefire-plugin用来执行单元测试`*Test.java`。

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>${maven-surefire-plugin.version}</version>
  <configuration>
    <skipTests>${skipUTs}</skipTests>
  </configuration>
</plugin>
```

## 执行

三种常见用法：

* `mvn clean integration-test`，会启动docker container、运行集成测试。这个很有用，如果集成测试失败，那么你还可以连接到MySQL数据库查看情况。
* `mvn clean verify`，会执行`mvn integration-test`、删除docker container。
* `mvn clean install`，会执`mvn verify`，并将包安装到本地maven 仓库。

配合`-DskipTests`、`-DskipITs`、`-DskipUTs`可以控制是否跳过所有测试、紧跳过集成测试、仅跳过单元测试。

下面是`mvn clean verify`的日志：

```txt
...
[INFO] --- docker-maven-plugin:0.28.0:start (start) @ spring-test-examples-rdbs-docker ---
[INFO] DOCKER> [mysql:8]: Start container f683aadfe8ba
[INFO] DOCKER> Pattern 'MySQL init process done. Ready for start up.' matched for container f683aadfe8ba
[INFO] DOCKER> [mysql:8]: Waited on log out 'MySQL init process done. Ready for start up.' 13717 ms
[INFO]
[INFO] --- maven-failsafe-plugin:2.22.1:integration-test (integration-test) @ spring-test-examples-rdbs-docker ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
...
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- docker-maven-plugin:0.28.0:stop (stop) @ spring-test-examples-rdbs-docker ---
[INFO] DOCKER> [mysql:8]: Stop and removed container f683aadfe8ba after 0 ms
[INFO]
[INFO] --- maven-failsafe-plugin:2.22.1:verify (verify) @ spring-test-examples-rdbs-docker ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
...
```

可以看到fabric8 dmp在集成测试前后start和stop容器的相关日志，且测试成功。

如何找到MySQL的端口开在哪一个呢？运行`docker ps`查看端口（注意下面的`0.0.0.0:32798->3306/tcp`）：

```txt
CONTAINER ID  IMAGE     COMMAND  CREATED  STATUS    PORTS                                NAMES
a1f4b51d7c75  mysql:8   ...      ...      Up 19...  33060/tcp, 0.0.0.0:32798->3306/tcp   mysql-1
```

## 在Intellij IDEA中运行JUnit的问题

本文中用到的pom.xml同样可适用于JUnit编写的单元、集成测试。

在Intellij IDEA中直接执行JUnit测试的时候会读取maven-failsafe-plugin的`systemPropertyVariables`，这样会产生问题：
你想用一个临时的application.properties来执行JUnit测试，但是IDEA的JUnit插件读取了maven-failsafe-plugin的`systemPropertyVariables`，
而这些东西覆盖了application.properties里的内容，并且它们的值还可能是错的。

解决办法是将maven-failsafe-plugin和docker-maven-plugin放到profile中：

```xml
  <profiles>
    <profile>
      <id>integration-test</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>${maven-failsafe-plugin.version}</version>
            ...
          </plugin>

          <plugin>
            <groupId>io.fabric8</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>${fabric8.dmp.version}</version>
            ...
          </plugin>

        </plugins>
      </build>
    </profile>
  </profiles>
```

然后使用`mvn clean install -Pintegration-test`来启用它。

## 参考文档

* [Fabric8 dmp][fabric8-dmp]
* [Spring boot - Externalized Configuration][doc-spring-boot-ext-config]


[chapter_4_s1_basic]: chapter_4_s1_basic.md
[code-rdbs-docker]:rdbs-docker
[fabric8-dmp]: https://dmp.fabric8.io/

[fabric8-dmp-build]: https://dmp.fabric8.io/#docker:build
[fabric8-dmp-start]: https://dmp.fabric8.io/#docker:start

[doc-spring-boot-ext-config]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/html/boot-features-external-config.html
