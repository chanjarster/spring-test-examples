# Chapter 2: Annotations - @TestPropertySource

[@TestPropertySource][javadoc-TestPropertySource]可以用来覆盖掉来自于系统环境变量、Java系统属性、[@PropertySource][javadoc-PropertySource]的属性。

同时``@TestPropertySource(properties=...)``优先级高于``@TestPropertySource(locations=...)``。

利用它我们可以很方便的在测试代码里微调、模拟配置（比如修改操作系统目录分隔符、数据源等）。

## 例子1: 使用Spring Testing工具

我们先使用[@PropertySource][javadoc-PropertySource]将一个外部properties文件加载进来，[PropertySourceConfig][src-ex1-PropertySourceConfig]：

```java
@Configuration
@PropertySource("classpath:me/chanjar/annotation/testps/ex1/property-source.properties")
public class PropertySourceConfig {
}
```

```
file: property-source.properties
foo=abc
```

然后我们用[@TestPropertySource][javadoc-TestPropertySource]覆盖了这个property:

```java
@TestPropertySource(properties = { "foo=xyz" ...
```

最后我们测试了是否覆盖成功（结果是成功的）：

```java
@Test
public void testOverridePropertySource() {
  assertEquals(environment.getProperty("foo"), "xyz");
}
```

同时我们还对@TestPropertySource做了一些其他的测试，具体情况你可以自己观察。为了方便你观察[@TestPropertySource][javadoc-TestPropertySource]对系统环境变量和Java系统属性的覆盖效果，我们在一开始打印出了它们的值。

源代码[TestPropertyTest][src-ex1-TestPropertyTest]：

```java
@ContextConfiguration(classes = PropertySourceConfig.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:me/chanjar/annotation/testps/ex1/test-property-source.properties"
)
public class TestPropertyTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {

  private Environment environment;

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
    Map<String, Object> systemEnvironment = ((ConfigurableEnvironment) environment).getSystemEnvironment();
    System.out.println("=== System Environment ===");
    System.out.println(getMapString(systemEnvironment));
    System.out.println();

    System.out.println("=== Java System Properties ===");
    Map<String, Object> systemProperties = ((ConfigurableEnvironment) environment).getSystemProperties();
    System.out.println(getMapString(systemProperties));
  }

  @Test
  public void testOverridePropertySource() {
    assertEquals(environment.getProperty("foo"), "xyz");
  }

  @Test
  public void testOverrideSystemEnvironment() {
    assertEquals(environment.getProperty("PATH"), "aaa");
  }

  @Test
  public void testOverrideJavaSystemProperties() {
    assertEquals(environment.getProperty("java.runtime.name"), "bbb");
  }

  @Test
  public void testInlineTestPropertyOverrideResourceLocationTestProperty() {
    assertEquals(environment.getProperty("bar"), "uvw");
  }

  private String getMapString(Map<String, Object> map) {
    return String.join("\n",
        map.keySet().stream().map(k -> k + "=" + map.get(k)).collect(toList())
    );
  }
}
```

## 例子2: 使用Spring Boot Testing工具

[@TestPropertySource][javadoc-TestPropertySource]也可以和[@SpringBootTest][javadoc-SpringBootTest]一起使用。

源代码见[TestPropertyTest][src-ex2-TestPropertyTest]：

```java
@SpringBootTest(classes = PropertySourceConfig.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:me/chanjar/annotation/testps/ex1/test-property-source.properties"
)
public class TestPropertyTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {
  // ... 
}
```



## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Context configuration with test property sources][doc-test-property-source]

[src-ex1-TestPropertyTest]: annotation/src/test/java/me/chanjar/annotation/testps/ex1/TestPropertyTest.java
[src-ex1-PropertySourceConfig]: annotation/src/test/java/me/chanjar/annotation/testps/ex1/PropertySourceConfig.java
[src-ex2-TestPropertyTest]: annotation/src/test/java/me/chanjar/annotation/testps/ex2/TestPropertyTest.java
[src-ex2-TestPropertyTest]: annotation/src/test/java/me/chanjar/annotation/testps/ex2/TestPropertyTest.java
[javadoc-SpringBootTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/SpringBootTest.html
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[javadoc-TestPropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/TestPropertySource.html
[javadoc-PropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/PropertySource.html
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[doc-test-property-source]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-ctx-management-property-sources
