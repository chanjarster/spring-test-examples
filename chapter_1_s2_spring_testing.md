# Chapter 1: 基本用法 - 使用Spring Testing工具

既然我们现在开发的是一个Spring项目，那么肯定会用到Spring Framework的各种特性，这些特性实在是太好用了，它能够大大提高我们的开发效率。那么自然而然，你会想在测试代码里也能够利用Spring Framework提供的特性，来提高测试代码的开发效率。这部分我们会讲如何使用Spring提供的测试工具来做测试。

## 例子1

源代码见[FooServiceImplTest][src-ex1-FooServiceImplTest]：

```java
@ContextConfiguration(classes = FooServiceImpl.class)
public class FooServiceImplTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
```

在上面的源代码里我们要注意三点：

1. 测试类继承了[AbstractTestNGSpringContextTests][javadoc-AbstractTestNGSpringContextTests]，如果不这么做测试类是无法启动Spring容器的
1. 使用了[@ContextConfiguration][javadoc-ContextConfiguration]来加载被测试的Bean：`FooServiceImpl`
1. `FooServiceImpl`是`@Component`

以上三点缺一不可。

## 例子2

在这个例子里，我们将`@Configuration`作为nested static class放在测试类里，根据[@ContextConfiguration][doc-spring-ContextConfiguration]的文档，它会在默认情况下查找测试类的nested static @Configuration class，用它来导入Bean。

源代码见[FooServiceImplTest][src-ex2-FooServiceImplTest]：


```java
@ContextConfiguration
public class FooServiceImplTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

  @Configuration
  @Import(FooServiceImpl.class)
  static class Config {
  }

}
```


## 例子3

在这个例子里，我们将`@Configuration`放到外部，并让[@ContextConfiguration][doc-spring-ContextConfiguration]去加载。

源代码见[Config][src-ex3-Config]：

```java
@Configuration
@Import(FooServiceImpl.class)
public class Config {
}
```

[FooServiceImplTest][src-ex3-FooServiceImplTest]：

```java
@ContextConfiguration(classes = Config.class)
public class FooServiceImplTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
```

需要注意的是，如果`@Configuration`是专供某个测试类使用的话，把它放到外部并不是一个好主意，因为它有可能会被`@ComponentScan`扫描到，从而产生一些奇怪的问题。

## 参考文档

* [Spring Framework Testing][doc-spring-framework-testing]
* [Context configuration with annotated classes][doc-spring-ContextConfiguration]   

[doc-spring-ContextConfiguration]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-ctx-management-javaconfig
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[javadoc-AbstractTestNGSpringContextTests]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/testng/AbstractTestNGSpringContextTests.html
[src-ex1-FooServiceImplTest]: basic/src/test/java/me/chanjar/basic/spring/ex1/FooServiceImplTest.java
[src-ex2-FooServiceImplTest]: basic/src/test/java/me/chanjar/basic/spring/ex2/FooServiceImplTest.java
[src-ex3-Config]: basic/src/test/java/me/chanjar/basic/spring/ex3/Config.java
[src-ex3-FooServiceImplTest]: basic/src/test/java/me/chanjar/basic/spring/ex3/FooServiceImplTest.java





