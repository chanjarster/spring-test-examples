# Chapter 2: Annotation测试工具

Spring & Spring Boot提供了一些测试相关的Annotation工具供使用，下面对这些Annotation做一些讲解。

## Section 1: @TestPropertySource

[@TestPropertySource][javadoc-TestPropertySource]可以用来覆盖掉来自于系统环境变量、Java系统属性、[@PropertySource][javadoc-PropertySource]的属性。

同时``@TestPropertySource(properties=...)``优先级高于``@TestPropertySource(locations=...)``。

### 例子1: With Spring Boot Testing

源代码见[TestPropertyNoBootTest][src-TestPropertyNoBootTest]和[PropertySourceConfiguration][src-PropertySourceConfiguration]：

```java
@Configuration
@PropertySource("classpath:/me/chanjar/section1/noboot/property-source.properties")
public class PropertySourceConfiguration {
}

@ContextConfiguration(classes = PropertySourceConfiguration.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:/me/chanjar/section1/noboot/test-property-source.properties"
)
public class TestPropertyNoBootTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {

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

然后在[TestPropertyNoBootTest][src-TestPropertyNoBootTest]中我们用[@TestPropertySource][javadoc-TestPropertySource]覆盖了这个property:

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

### 例子2: With Spring Testing

[@TestPropertySource][javadoc-TestPropertySource]也可以和[@SpringBootTest][javadoc-SpringBootTest]一起使用。

源代码见[TestPropertyBootTest][src-TestPropertyBootTest]：

```java
@SpringBootTest(classes = PropertySourceConfiguration.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:/me/chanjar/section1/noboot/test-property-source.properties"
)
public class TestPropertyBootTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {
  // ...
}
```


## Section 2: @OverrideAutoConfiguration

[@OverrideAutoConfiguration][javadoc-OverrideAutoConfiguration]是Spring Boot的Annotation，在Chapter 1中已经见到过，它可以用来设定是否开启Auto Configuration。

## Section 3: @JsonTest

[@JsonTest][javadoc-JsonTest]是Spring Boot提供的方便测试JSON序列化反序列化的测试工具，在Spring Boot的[文档][doc-JsonTest]中有一些介绍。

需要注意的是[@JsonTest][javadoc-JsonTest]需要Jackson的``ObjectMapper``而``ObjectMapper``可以通过`spring-boot-autoconfigure`中的[JacksonAutoConfiguration][javadoc-JacksonAutoConfiguration]来自动配置，不过前提是添加了`spring-web`的依赖：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-web</artifactId>
</dependency>
```

这里没有提供关于日期时间的例子，关于这个比较复杂，可以看我的另一篇文章：[Spring Boot Jackson对于日期时间类型处理的例子][github-springboot-jackson-datetime-example]。

### 例子1：@SpringBootTest

源代码见[SimpleJsonTest][src-SimpleJsonTest]：

```java
@SpringBootTest(classes = SimpleJsonTest.class)
@JsonTest
public class SimpleJsonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    Foo details = new Foo("Honda", 12);
    // 使用通包下的json文件测试结果是否正确
    assertThat(this.json.write(details)).isEqualToJson("expected.json");
    // 或者使用基于JSON path的校验
    assertThat(this.json.write(details)).hasJsonPathStringValue("@.name");
    assertThat(this.json.write(details)).extractingJsonPathStringValue("@.name").isEqualTo("Honda");
    assertThat(this.json.write(details)).hasJsonPathNumberValue("@.age");
    assertThat(this.json.write(details)).extractingJsonPathNumberValue("@.age").isEqualTo(12);
  }

  @Test
  public void testDeserialize() throws Exception {
    String content = "{\"name\":\"Ford\",\"age\":13}";
    Foo actual = this.json.parseObject(content);
    assertThat(actual).isEqualTo(new Foo("Ford", 13));
    assertThat(actual.getName()).isEqualTo("Ford");
    assertThat(actual.getAge()).isEqualTo(13);

  }

}
```

### 例子2: @SpringBootTest & @JsonComponent

[@JsonTest][javadoc-JsonTest]可以用来测试[@JsonComponent][javadoc-JsonComponent]，这个例子里使用了自定义的``@JsonComponent`` [FooJsonComponent][src-FooJsonComponent]，并且提供了相应的测试代码。

源代码见[JsonComponentJsonTest][src-JsonComponentJsonTest]：

```java
@SpringBootTest(classes = { JsonComponentJacksonTest.class, FooJsonComponent.class })
@JsonTest
public class JsonComponentJacksonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    Foo details = new Foo("Honda", 12);
    assertThat(this.json.write(details).getJson()).isEqualTo("\"name=Honda,age=12\"");
  }

  @Test
  public void testDeserialize() throws Exception {
    String content = "\"name=Ford,age=13\"";
    Foo actual = this.json.parseObject(content);
    assertThat(actual).isEqualTo(new Foo("Ford", 13));
    assertThat(actual.getName()).isEqualTo("Ford");
    assertThat(actual.getAge()).isEqualTo(13);

  }

}
```

### 例子3: 不使用@SpringBootTest

因为[@JsonTest][javadoc-JsonTest]本身已经定义了``@BootstrapWith(value=SpringBootTestContextBootstrapper.class)``所以可以直接使用它来测试。

源代码见[ThinJsonTest][src-ThinJsonTest]：

```java
@JsonTest
@ContextConfiguration(classes = JsonTest.class)
public class ThinJsonTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private JacksonTester<Foo> json;

  @Test
  public void testSerialize() throws Exception {
    // ...
  }

  @Test
  public void testDeserialize() throws Exception {
    // ...
  }

}

```

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[javadoc-TestPropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/TestPropertySource.html
[javadoc-OverrideAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/OverrideAutoConfiguration.html
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[javadoc-PropertySource]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/PropertySource.html
[javadoc-SpringBootTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/SpringBootTest.html
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[doc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-json-tests
[src-TestPropertyNoBootTest]: annotation/src/test/java/me/chanjar/section1/noboot/TestPropertyNoBootTest.java
[src-PropertySourceConfiguration]: annotation/src/test/java/me/chanjar/section1/noboot/PropertySourceConfiguration.java
[src-TestPropertyBootTest]: annotation/src/test/java/me/chanjar/section1/boot/TestPropertyBootTest.java
[javadoc-JacksonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/JacksonTester.html
[javadoc-GsonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/GsonTester.html
[javadoc-BasicJsonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/BasicJsonTester.html
[javadoc-JacksonAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration.html
[github-springboot-jackson-datetime-example]: https://github.com/chanjarster/springboot-jackson-datetime-example
[src-SimpleJsonTest]: src/test/java/me/chanjar/section3/boot1/SimpleJsonTest.java
[src-JsonComponentJsonTest]: src/test/java/me/chanjar/section3/boot2/JsonComponentJsonTest.java
[src-FooJsonComponent]: src/test/java/me/chanjar/section3/boot2/FooJsonComponent.java
[javadoc-JsonComponent]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/jackson/JsonComponent.html
[src-ThinJsonTest]: src/test/java/me/chanjar/section3/boot3/ThinJsonTest.java
