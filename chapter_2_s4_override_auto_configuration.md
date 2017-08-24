# Chapter 2: Annotations - @OverrideAutoConfiguration

在[Chapter 1: 基本用法 - 使用Spring Boot Testing工具][chapter_1_s3_spring_boot_testing.md]里提到：

> 除了单元测试（不需要初始化ApplicationContext的测试）外，尽量将测试配置和生产配置保持一致。比如如果生产配置里启用了AutoConfiguration，那么测试配置也应该启用。因为只有这样才能够在测试环境下发现生产环境的问题，也避免出现一些因为配置不同导致的奇怪问题。

那么当我们想在测试代码里关闭Auto Configuration如何处理？

1. 方法1：提供另一套测试配置
1. 方法2：使用`@OverrideAutoConfiguration`

方法1虽然能够很好的解决问题，但是比较麻烦。而方法2则能够不改变原有配置、不提供新的配置的情况下，就能够关闭Auto Configuration。

在本章节的例子里，我们自己做了一个Auto Configuration类，[AutoConfigurationEnableLogger][src-AutoConfigurationEnableLogger]：

```java
@Configuration
public class AutoConfigurationEnableLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutoConfigurationEnableLogger.class);

  public AutoConfigurationEnableLogger() {
    LOGGER.info("Auto Configuration Enabled");
  }

}
```

并且在`META-INF/spring.factories`里注册了它：

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
me.chanjar.annotation.overrideac.AutoConfigurationEnableLogger
```

这样一来，只要Spring Boot启动了Auto Configuration就会打印出日志：

```
2017-08-24 16:44:52.789  INFO 13212 --- [           main] m.c.a.o.AutoConfigurationEnableLogger    : Auto Configuration Enabled
```

## 例子1：未关闭Auto Configuration

源代码见[BootTest][src-ex1-BootTest]：

```java
@SpringBootTest
@SpringBootApplication
public class BootTest extends AbstractTestNGSpringContextTests {

  @Test
  public void testName() throws Exception {

  }
}
```

查看输出的日志，会发现Auto Configuration已经启用。

## 例子2：关闭Auto Configuration

然后我们用[@OverrideAutoConfiguration][javadoc-OverrideAutoConfiguration]关闭了Auto Configuration。

源代码见[BootTest][src-ex2-BootTest]：

```java
@SpringBootTest
@OverrideAutoConfiguration(enabled = false)
@SpringBootApplication
public class BootTest extends AbstractTestNGSpringContextTests {

  @Test
  public void testName() throws Exception {

  }
}
```

再查看输出的日志，就会发现Auto Configuration已经关闭。


## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Context configuration with test property sources][doc-test-property-source]

[chapter_1_s3_spring_boot_testing.md]: chapter_1_s3_spring_boot_testing.md
[javadoc-OverrideAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/OverrideAutoConfiguration.html

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[src-AutoConfigurationEnableLogger]: annotation/src/main/java/me/chanjar/annotation/overrideac/AutoConfigurationEnableLogger.java
[src-ex1-BootTest]: annotation/src/test/java/me/chanjar/annotation/overrideac/ex1/BootTest.java
[src-ex2-BootTest]: annotation/src/test/java/me/chanjar/annotation/overrideac/ex2/BootTest.java
