# Chapter 2: Annotations - @TestConfiguration

[@TestConfiguration][javadoc-test-configuration]是Spring Boot Test提供的一种工具，用它我们可以在一般的@Configuration之外补充测试专门用的Bean或者自定义的配置。

[@TestConfiguration][javadoc-test-configuration]实际上是一种[@TestComponent][javadoc-test-component]，[@TestComponent][javadoc-test-component]是另一种@Component，在语义上用来指定某个Bean是专门用于测试的。

需要特别注意，你应该使用一切办法避免在生产代码中自动扫描到[@TestComponent][javadoc-test-component]。
如果你使用`@SpringBootApplication`启动测试或者生产代码，[@TestComponent][javadoc-test-component]会自动被排除掉，如果不是则需要像`@SpringBootApplication`一样添加`TypeExcludeFilter`：

```java
//...
@ComponentScan(excludeFilters = {
  @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
  // ...})
public @interface SpringBootApplication
```

## 例子1：作为内部类

[@TestConfiguration][javadoc-test-configuration]和@Configuration不同，它不会阻止@SpringBootTest去查找机制（在[Chapter 1: 基本用法 - 使用Spring Boot Testing工具 - 例子4][chapter_1_s3_spring_boot_testing]提到过），正如[@TestConfiguration][javadoc-test-configuration]的javadoc所说，它只是对既有配置的一个补充。

所以我们在测试代码上添加@SpringBootConfiguration，用`@SpringBootTest(classes=...)`或者在同package里添加@SpringBootConfiguration类都是可以的。

而且[@TestConfiguration][javadoc-test-configuration]作为内部类的时候它是会被@SpringBootTest扫描掉的，这点和@Configuration一样。

测试代码[TestConfigurationTest][src-ex1-TestConfigurationTest]：

```java
@SpringBootTest
@SpringBootConfiguration
public class TestConfigurationTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getName(), "from test config");
  }

  @TestConfiguration
  public class TestConfig {

    @Bean
    public Foo foo() {
      return new Foo("from test config");
    }

  }
}
```



## 例子2：对@Configuration的补充和覆盖

[@TestConfiguration][javadoc-test-configuration]能够：

1. 补充额外的Bean
1. 覆盖已存在的Bean

要特别注意第二点，[@TestConfiguration][javadoc-test-configuration]能够直接覆盖已存在的Bean，这一点正常的@Configuration是做不到的。

我们先提供了一个正常的@Configuration（[Config][src-ex2-Config]）：

```java
@Configuration
public class Config {

  @Bean
  public Foo foo() {
    return new Foo("from config");
  }
}
```

又提供了一个@TestConfiguration，在里面覆盖了`foo` Bean，并且提供了`foo2` Bean（[TestConfig][src-ex2-TestConfig]）：

```java
@TestConfiguration
public class TestConfig {

  // 这里不需要@Primary之类的机制，直接就能够覆盖
  @Bean
  public Foo foo() {
    return new Foo("from test config");
  }

  @Bean
  public Foo foo2() {
    return new Foo("from test config2");
  }
}
```

测试代码[TestConfigurationTest][src-ex2-TestConfigurationTest]：

```java
@SpringBootTest(classes = { Config.class, TestConfig.class })
public class TestConfigurationTest extends AbstractTestNGSpringContextTests {

  @Qualifier("foo")
  @Autowired
  private Foo foo;

  @Qualifier("foo2")
  @Autowired
  private Foo foo2;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getName(), "from test config");
    assertEquals(foo2.getName(), "from test config2");

  }

}
```

再查看输出的日志，就会发现Auto Configuration已经关闭。

## 例子3：避免@TestConfiguration被扫描到

在上面的这个例子里的[TestConfig][src-ex2-TestConfig]是会被@ComponentScan扫描到的，如果要避免被扫描到，在本文开头已经提到过了。

先来看一下没有做任何过滤的情形，我们先提供了一个@SpringBootConfiguration（[IncludeConfig][src-ex3-IncludeConfig]）：

```java
@SpringBootConfiguration
@ComponentScan
public interface IncludeConfig {
}
```

然后有个测试代码引用了它（[TestConfigIncludedTest][src-ex3-TestConfigIncludedTest]）：

```java
@SpringBootTest(classes = IncludeConfig.class)
public class TestConfigIncludedTest extends AbstractTestNGSpringContextTests {

  @Autowired(required = false)
  private TestConfig testConfig;

  @Test
  public void testPlusCount() throws Exception {
    assertNotNull(testConfig);

  }

}
```

从这段代码可以看到`TestConfig`被加载了。

现在我们使用TypeExcludeFilter来过滤@TestConfiguration（[ExcludeConfig1][src-ex3-ExcludeConfig1]）：

```java
@SpringBootConfiguration
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class)
})
public interface ExcludeConfig1 {
}
```

再来看看结果（[TestConfigExclude_1_Test][src-ex3-TestConfigExclude_1_Test]）：

```java
@SpringBootTest(classes = ExcludeConfig1.class)
public class TestConfigExclude_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired(required = false)
  private TestConfig testConfig;

  @Test
  public void test() throws Exception {
    assertNull(testConfig);

  }

}
```

还可以用@SpringBootApplication来排除`TestConfig`（[ExcludeConfig2][src-ex3-ExcludeConfig2]）：

```java
@SpringBootApplication
public interface ExcludeConfig2 {
}
```

看看结果（[TestConfigExclude_2_Test][src-ex3-TestConfigExclude_2_Test]）：

```java
@SpringBootTest(classes = ExcludeConfig2.class)
public class TestConfigExclude_2_Test extends AbstractTestNGSpringContextTests {

  @Autowired(required = false)
  private TestConfig testConfig;

  @Test
  public void testPlusCount() throws Exception {
    assertNull(testConfig);

  }

}
```

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Detecting test configuration][doc-spring-boot-detecting-test-configuration]
* [Excluding test configuration][doc-spring-boot-excluding-test-configuration]

[chapter_1_s3_spring_boot_testing]: chapter_1_s3_spring_boot_testing.md

[javadoc-test-configuration]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/TestConfiguration.html
[javadoc-test-component]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/TestComponent.html
[src-ex1-TestConfigurationTest]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex1/TestConfigurationTest.java
[src-ex2-Config]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex2/Config.java
[src-ex2-TestConfig]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex2/TestConfig.java
[src-ex2-TestConfigurationTest]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex2/TestConfigurationTest.java
[src-ex3-ExcludeConfig1]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/ExcludeConfig1.java
[src-ex3-ExcludeConfig2]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/ExcludeConfig2.java
[src-ex3-IncludeConfig]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/IncludeConfig.java
[src-ex3-TestConfig]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/TestConfig.java
[src-ex3-TestConfigExclude_1_Test]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/TestConfigExclude_1_Test.java
[src-ex3-TestConfigExclude_2_Test]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/TestConfigExclude_2_Test.java
[src-ex3-TestConfigIncludedTest]: annotation/src/test/java/me/chanjar/annotation/testconfig/ex3/TestConfigIncludedTest.java

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[doc-spring-boot-detecting-test-configuration]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-detecting-config
[doc-spring-boot-excluding-test-configuration]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-excluding-config
