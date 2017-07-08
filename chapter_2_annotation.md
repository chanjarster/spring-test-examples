# Chapter 2: Annotation测试工具

Spring & Spring Boot提供了一些测试相关的Annotation工具供使用，下面对这些Annotation做一些讲解。

## Section 1: @TestPropertySource

[@TestPropertySource][javadoc-TestPropertySource]可以用来覆盖掉来自于系统环境变量、Java系统属性、[@PropertySource][javadoc-PropertySource]的属性。

同时``@TestPropertySource(properties=...)``优先级高于``@TestPropertySource(locations=...)``。

源代码见[TestPropertyOverrideTest][src-TestPropertyOverrideTest]和[PropertySourceConfiguration][src-PropertySourceConfiguration]：

```java
@Configuration
@PropertySource("classpath:/me/chanjar/section1/test_property_source/property-source.properties")
public class PropertySourceConfiguration {
}

@ContextConfiguration(classes = PropertySourceConfiguration.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:/me/chanjar/section1/test_property_source/test-property-source.properties"
)
public class TestPropertyOverrideTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {

  private Environment environment;

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
}
```

我们先测试了对[@PropertySource][javadoc-PropertySource]的覆盖效果。首先，[PropertySourceConfiguration][src-PropertySourceConfiguration]加载了property-source.properties文件，这个文件定义了一个property：

```
foo=abc
```

然后在[TestPropertyOverrideTest][src-TestPropertyOverrideTest]中我们用[@TestPropertySource][javadoc-TestPropertySource]覆盖了这个property:

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

其余的覆盖结果你可以自己看。为了方便你观察[@TestPropertySource][javadoc-TestPropertySource]对系统环境变量和Java系统属性的覆盖效果，我们在一开始打印出了它们的值：

```java
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
```

## @OverrideAutoConfiguration

[@OverrideAutoConfiguration][javadoc-OverrideAutoConfiguration]是Spring Boot的Annotation，在Chapter 1中已经见到过，它可以用来设定是否开启Auto Configuration。

## @JsonTest

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[javadoc-TestPropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/TestPropertySource.html
[javadoc-OverrideAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/OverrideAutoConfiguration.html
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[javadoc-PropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/PropertySource.html
[src-TestPropertyOverrideTest]: annotation/src/test/java/me/chanjar/section1/test_property_source/TestPropertyOverrideTest.java
[src-PropertySourceConfiguration]: annotation/src/test/java/me/chanjar/section1/test_property_source/PropertySourceConfiguration.java
