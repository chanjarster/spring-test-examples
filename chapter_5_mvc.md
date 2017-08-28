# Chapter 5: 测试Spring MVC

[Spring Testing Framework][doc-spring-framework-testing]提供了[Spring MVC Test Framework][doc-spring-mvc-test-framework]，能够很方便的来测试Controller。同时Spring Boot也提供了[Auto-configured Spring MVC tests][doc-auto-configured-spring-mvc-tests]更进一步简化了测试需要的配置工作。

本章节将分别举例说明在不使用Spring Boot和使用Spring Boot下如何对Spring MVC进行测试。

## 例子1：Spring

测试Spring MVC的关键是使用`MockMvc`对象，利用它我们能够在不需启动Servlet容器的情况下测试Controller的行为。

源代码[SpringMvc_1_Test.java][src-SpringMvc_1_Test.java]：

```java
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
public class SpringMvc_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mvc;

  @BeforeMethod
  public void prepareMockMvc() {
    this.mvc = webAppContextSetup(wac).build();
  }

  @Test
  public void testController() throws Exception {

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
```

在这段代码里，主要有三个步骤：

1. 将测试类标记为`@WebAppConfiguration`
1. 通过`webAppContextSetup(wac).build()`构建`MockMvc`
1. 利用`MockMvc`对结果进行判断

## 例子2：Spring + Mock

在例子1里，`FooController`使用了一个实体`FooImpl`的Bean，实际上我们也可以提供一个`Foo`的mock bean来做测试，这样就能够更多的控制测试过程。如果你还不知道Mock那么请看[Chapter 3: 使用Mockito][chapter_3_mockito]。

源代码[SpringMvc_2_Test.java][src-SpringMvc_2_Test.java]：

```java
@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringMvc_2_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private WebApplicationContext wac;

  @MockBean
  private Foo foo;

  private MockMvc mvc;

  @BeforeMethod
  public void prepareMockMvc() {
    this.mvc = webAppContextSetup(wac).build();
  }

  @Test
  public void testController() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
```


## 例子3：Spring Boot

Spring Boot提供了`@WebMvcTest`更进一步简化了对于Spring MVC的测试，我们提供了对应例子1的Spring Boot版本。

源代码[BootMvc_1_Test.java][src-BootMvc_1_Test.java]：

```java
@WebMvcTest
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
public class BootMvc_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testController() throws Exception {

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
```

在这里，我们不需要自己构建`MockMvc`，直接使用`@Autowired`注入就行了，是不是很方便？

## 例子4：Spring Boot + Mock

这个是对应例子2的Spring Boot版本，源代码[BootMvc_2_Test.java][src-BootMvc_2_Test.java]：

```java
@WebMvcTest
@ContextConfiguration(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class BootMvc_2_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Foo foo;

  @Test
  public void testController() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
```


## 参考文档

* [Loading a WebApplicationContext][doc-spring-WebApplicationContext]
* [Spring MVC Test Framework][doc-spring-mvc-test-framework]
* [Spring MVC Official Sample Tests][gh-spring-mvc-official-sample-tests]
* [Spring MVC showcase - with full mvc test][gh-spring-mvc-showcase]
* [Auto-configured Spring MVC tests][doc-auto-configured-spring-mvc-tests]
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Spring Guides - Testing the Web Layer][guide-testing-the-web-layer]

[chapter_3_mockito]: chapter_3_mockito.md
[guide-testing-the-web-layer]: https://spring.io/guides/gs/testing-web/
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-WebApplicationContext]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-ctx-management-web
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[javadoc-AutoConfigureMockMvc]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/web/servlet/AutoConfigureMockMvc.html
[doc-auto-configured-spring-mvc-tests]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-mvc-tests
[doc-spring-mvc-test-framework]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#spring-mvc-test-framework
[gh-spring-mvc-official-sample-tests]: https://github.com/spring-projects/spring-framework/tree/master/spring-test/src/test/java/org/springframework/test/web/servlet/samples
[gh-spring-mvc-showcase]: https://github.com/spring-projects/spring-mvc-showcase
[src-SpringMvc_1_Test.java]: mvc/src/test/java/me/chanjar/spring1/SpringMvc_1_Test.java
[src-SpringMvc_2_Test.java]: mvc/src/test/java/me/chanjar/spring2/SpringMvc_2_Test.java
[src-BootMvc_1_Test.java]: mvc/src/test/java/me/chanjar/springboot1/BootMvc_1_Test.java
[src-BootMvc_2_Test.java]: mvc/src/test/java/me/chanjar/springboot2/BootMvc_2_Test.java
