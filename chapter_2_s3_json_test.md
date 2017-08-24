# Chapter 2: Annotations - @JsonTest

[@JsonTest][javadoc-JsonTest]是Spring Boot提供的方便测试JSON序列化反序列化的测试工具，在Spring Boot的[文档][doc-JsonTest]中有一些介绍。

需要注意的是[@JsonTest][javadoc-JsonTest]需要Jackson的``ObjectMapper``，事实上如果你的Spring Boot项目添加了`spring-web`的Maven依赖，[JacksonAutoConfiguration][javadoc-JacksonAutoConfiguration]就会自动为你配置一个：

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

## 例子1：简单例子

源代码见[SimpleJsonTest][src-ex1-SimpleJsonTest]：

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

## 例子2: 测试@JsonComponent

[@JsonTest][javadoc-JsonTest]可以用来测试[@JsonComponent][javadoc-JsonComponent]。

这个例子里使用了自定义的``@JsonComponent`` [FooJsonComponent][src-ex2-FooJsonComponent]：

```java
@JsonComponent
public class FooJsonComponent {

  public static class Serializer extends JsonSerializer<Foo> {
    @Override
    public void serialize(Foo value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      // ...
    }

  }

  public static class Deserializer extends JsonDeserializer<Foo> {

    @Override
    public Foo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      // ...
    }

  }

}

```

测试代码[JsonComponentJsonTest][src-ex2-JsonComponentJsonTest]：

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

## 例子3: 使用@ContextConfiguration

事实上[@JsonTest][javadoc-JsonTest]也可以配合`@ContextConfiguration`一起使用。

源代码见[ThinJsonTest][src-ex3-ThinJsonTest]：

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
* [@JsonTest][doc-JsonTest]
* [JsonComponent][javadoc-JsonComponent]
* [JacksonAutoConfiguration][javadoc-JacksonAutoConfiguration]
* [JacksonTester][javadoc-JacksonTester]
* [GsonTester][javadoc-GsonTester]
* [BasicJsonTester][javadoc-BasicJsonTester]

[src-ex1-SimpleJsonTest]: annotation/src/test/java/me/chanjar/annotation/jsontest/ex1/SimpleJsonTest.java
[src-ex2-JsonComponentJsonTest]: annotation/src/test/java/me/chanjar/annotation/jsontest/ex2/JsonComponentJacksonTest.java
[src-ex2-FooJsonComponent]: annotation/src/test/java/me/chanjar/annotation/jsontest/ex2/FooJsonComponent.java
[src-ex3-ThinJsonTest]: annotation/src/test/java/me/chanjar/annotation/jsontest/ex3/ThinJsonTest.java
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[doc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-json-tests
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[javadoc-JsonTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html
[javadoc-JacksonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/JacksonTester.html
[javadoc-GsonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/GsonTester.html
[javadoc-BasicJsonTester]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/json/BasicJsonTester.html
[javadoc-JacksonAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/jackson/JacksonAutoConfiguration.html
[javadoc-JsonComponent]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/jackson/JsonComponent.html
[github-springboot-jackson-datetime-example]: https://github.com/chanjarster/springboot-jackson-datetime-example



