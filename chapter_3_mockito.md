# Chapter 3: 使用Mockito

Mock测试技术能够避免你为了测试一个方法，却需要自行构建整个依赖关系的工作，并且能够让你专注于当前被测试对象的逻辑，而不是其依赖的其他对象的逻辑。

举例来说，比如你需要测试`Foo.methodA`，而这个方法依赖了`Bar.methodB`，又传递依赖到了`Zoo.methodC`，于是它们的依赖关系就是`Foo->Bar->Zoo`，所以在测试代码里你必须自行new Bar和Zoo。

有人会说："我直接用Spring的DI机制不就行了吗？"的确，你可以用Spring的DI机制，不过解决不了测试代码耦合度过高的问题：

因为Foo方法内部调用了Bar和Zoo的方法，所以你对其做单元测试的时候，必须完全了解Bar和Zoo方法的内部逻辑，并且谨慎的传参和assert结果，一旦Bar和Zoo的代码修改了，你的Foo测试代码很可能就会运行失败。

所以这个时候我们需要一种机制，能过让我们在测试Foo的时候不依赖于Bar和Zoo的具体实现，即不关心其内部逻辑，只关注Foo内部的逻辑，从而将Foo的每个逻辑分支都测试到。

所以业界就产生了Mock技术，它可以让我们做一个假的Bar（不需要Zoo，因为只有真的Bar才需要Zoo），然后控制这个假的Bar的行为（让它返回什么就返回什么），以此来测试Foo的每个逻辑分支。

你肯定会问，这样的测试有意义吗？在真实环境里Foo用的是真的Bar而不是假的Bar，你用假的Bar测试成功能代表真实环境不出问题？

其实假Bar代表的是一个行为正确的Bar，用它来测试就能验证"在Bar行为正确的情况下Foo的行为是否正确"，而真Bar的行为是否正确会由它自己的测试代码来验证。

Mock技术的另一个好处是能够让你尽量避免集成测试，比如我们可以Mock一个Repository（数据库操作类），让我们尽量多写单元测试，提高测试代码执行效率。

`spring-boot-starter-test`依赖了[Mockito][site-mockito]，所以我们会在本章里使用Mockito来讲解。

## 被测试类

先介绍一下接下来要被我们测试的类[Foo][src-Foo]、[Bar][src-Bar]俩兄弟。

```java
public interface Foo {

  boolean checkCodeDuplicate(String code);

}

public interface Bar {

  Set<String> getAllCodes();

}

@Component
public class FooImpl implements Foo {

  private Bar bar;

  @Override
  public boolean checkCodeDuplicate(String code) {
    return bar.getAllCodes().contains(code);
  }

  @Autowired
  public void setBar(Bar bar) {
    this.bar = bar;
  }

}
```


## 例子1: 不使用Mock技术

源代码[NoMockTest][src-NoMockTest]：

```java
public class NoMockTest {

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    FooImpl foo = new FooImpl();
    foo.setBar(new Bar() {
      @Override
      public Set<String> getAllCodes() {
        return Collections.singleton("123");
      }
    });
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    FooImpl foo = new FooImpl();
    foo.setBar(new FakeBar(Collections.singleton("123")));
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

  public class FakeBar implements Bar {

    private final Set<String> codes;

    public FakeBar(Set<String> codes) {
      this.codes = codes;
    }

    @Override
    public Set<String> getAllCodes() {
      return codes;
    }

  }

}
```

这个测试代码里用到了两种方法来做假的Bar：

1. 匿名内部类
1. 做了一个``FakeBar``

这两种方式都不是很优雅，看下面使用Mockito的例子。

## 例子2：使用Mockito

源代码[MockitoTest][src-MockitoTest]：

```java
public class MockitoTest {

  @Mock
  private Bar bar;

  @InjectMocks
  private FooImpl foo;

  @BeforeMethod(alwaysRun = true)
  public void initMock() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCheckCodeDuplicate() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

}
```

1. 我们先给了一个Bar的Mock实现：`@Mock private Bar bar;`
1. 然后又规定了`getAllCodes`方法的返回值：`when(bar.getAllCodes()).thenReturn(Collections.singleton("123"))`。这样就把一个假的Bar定义好了。
1. 最后利用Mockito把Bar注入到Foo里面，`@InjectMocks private FooImpl foo;`、`MockitoAnnotations.initMocks(this);`


## 例子3：配合Spring Test

源代码[SpringTest][src-SpringTest]：

```java
@ContextConfiguration(classes = FooImpl.class)
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringTest extends AbstractTestNGSpringContextTests {

  @MockBean
  private Bar bar;

  @Autowired
  private Foo foo;

  @Test
  public void testCheckCodeDuplicate() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

}
```

要注意，如果要启用Spring和Mockito，必须添加这么一行：`@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)`。

## 例子4：撇号Spring Boot Test


源代码[BootTest][src-BootTest]：

```java
@SpringBootTest(classes = { FooImpl.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class BootTest extends AbstractTestNGSpringContextTests {

  @MockBean
  private Bar bar;

  @Autowired
  private Foo foo;

  @Test
  public void testCheckCodeDuplicate() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

}
```

## MVC结合Mockito测试

这里讲讲MVC的Mock测试怎么做，先来看要被我们测试的[FooController][src-FooController]：

```java
@Controller
public class FooController {

  @Autowired
  private Foo foo;

  @RequestMapping(path = "/foo/check-code-dup", method = RequestMethod.GET)
  public ResponseEntity<Boolean> checkCodeDuplicate(@RequestParam String code) {

    return new ResponseEntity<>(
        Boolean.valueOf(foo.checkCodeDuplicate(code)),
        HttpStatus.OK
    );

  }

}
```

这个Controller里使用了[Foo][src-Foo]。

### 例子1：Spring MVC 2

源代码[SpringMvc1Test][src-SpringMvc1Test]：

```java
@WebMvcTest(FooController.class)
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringMvc1Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Bar bar;

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("321"));

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

}
```

这个例子里，[FooController][src-FooController]使用的是真实的[FooImpl][src-FooImpl]，它使用了Mock [Bar][src-Bar]。其实我们可以直接使用Mock [Foo][src-Foo]来测试，见下面的例子。

我们通过`@WebMvcTest(FooController.class)`来启用Mvc的Mock测试，这种测试是纯内存的，不会开启网络端口。然后利用`MockMvc`来做测试。

### 例子2：Spring MVC 2

源代码[SpringMvc2Test][src-SpringMvc2Test]：


```java
@WebMvcTest(FooController.class)
@ContextConfiguration(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringMvc2Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Foo foo;

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(false);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

}
```

### 例子3：Spring Boot MVC

源代码[BootMvcTest][src-BootMvcTest]：

```java
@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class BootMvcTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Foo foo;

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(false);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

}
```

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Spring Boot Auto-configured Spring MVC tests][doc-web-mvc-test]
* [Mockito][site-mockito]

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[site-mockito]: http://site.mockito.org
[src-Bar]: mock/src/main/java/me/chanjar/common/Bar.java
[src-Foo]: mock/src/main/java/me/chanjar/common/Foo.java
[src-FooController]: mock/src/main/java/me/chanjar/common/FooController.java
[src-FooImpl]: mock/src/main/java/me/chanjar/common/FooImpl.java
[src-FakeBar]: mock/src/test/java/me/chanjar/no_mock/FakeBar.java
[src-NoMockTest]: mock/src/test/java/me/chanjar/no_mock/NoMockTest.java
[src-SpringTest]: mock/src/test/java/me/chanjar/spring/SpringTest.java
[src-BootTest]: mock/src/test/java/me/chanjar/springboot/BootTest.java
[src-BootMvcTest]: mock/src/test/java/me/chanjar/springbootmvc/BootMvcTest.java
[src-SpringMvc1Test]: mock/src/test/java/me/chanjar/springmvc/SpringMvc1Test.java
[src-SpringMvc2Test]: mock/src/test/java/me/chanjar/springmvc/SpringMvc2Test.java
[doc-web-mvc-test]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-mvc-tests
