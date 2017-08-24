# Chapter 1: 基本用法 - 使用Spring Boot Testing工具

前面一个部分讲解了如何使用Spring Testing工具来测试Spring项目，现在我们讲解如何使用Spring Boot Testing工具来测试Spring Boot项目。

> 在Spring Boot项目里既可以使用Spring Boot Testing工具，也可以使用Spring Testing工具。
> 在Spring项目里，一般使用Spring Testing工具，虽然理论上也可以使用Spring Boot Testing，不过因为Spring Boot Testing工具会引入Spring Boot的一些特性比如AutoConfiguration，这可能会给你的测试带来一些奇怪的问题，所以一般不推荐这样做。

## 例子1：直接加载Bean

使用Spring Boot Testing工具只需要将`@ContextConfiguration`改成`@SpringBootTest`即可，源代码见[FooServiceImpltest][src-ex1-FooServiceImpltest]：

```java
@SpringBootTest(classes = FooServiceImpl.class)
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

## 例子2：使用内嵌@Configuration加载Bean

源代码见[FooServiceImpltest][src-ex2-FooServiceImpltest]：

```java
@SpringBootTest
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

## 例子3：使用外部@Configuration加载Bean

[Config][src-ex3-Config]：

```
@Configuration
@Import(FooServiceImpl.class)
public class Config {
}
```

[FooServiceImpltest][src-ex3-FooServiceImpltest]：

```java
@SpringBootTest(classes = Config.class)
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

这个例子和例子2差不多，只不过将[@Configuration][javadoc-Configuration]放到了外部。

## 例子4：使用@SpringBootConfiguration

前面的例子`@SpringBootTest`的用法和`@ContextConfiguration`差不多。不过根据`@SpringBootTest`的[文档][javadoc-SpringBootTest]：

1. 它会尝试加载`@SpringBootTest(classes=...)`的定义的Annotated classes。Annotated classes的定义在[ContextConfiguration][javadoc-ContextConfiguration]中有说明。
1. 如果没有设定`@SpringBootTest(classes=...)`，那么会去找当前测试类的nested @Configuration class
1. 如果上一步找到，则会尝试查找`@SpringBootConfiguration`，查找的路径有：1)看当前测试类是否`@SpringBootConfiguration`，2)在当前测试类所在的package里找。

所以我们可以利用这个特性来进一步简化测试代码。

[Config][src-ex4-Config]：

```java
@SpringBootConfiguration
@Import(FooServiceImpl.class)
public class Config {
}
```

[FooServiceImpltest][src-ex4-FooServiceImpltest]：

```java
@SpringBootTest
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

## 例子5：使用@ComponentScan扫描Bean

前面的例子我们都使用`@Import`来加载Bean，虽然这中方法很精确，但是在大型项目中很麻烦。

在常规的Spring Boot项目中，一般都是依靠自动扫描机制来加载Bean的，所以我们希望我们的测试代码也能够利用自动扫描机制来加载Bean。

[Config][src-ex5-Config]：

```java
@SpringBootConfiguration
@ComponentScan(basePackages = "me.chanjar.basic.service")
public class Config {
}
```

[FooServiceImpltest][src-ex5-FooServiceImpltest]：

```java
@SpringBootTest
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

## 例子6：使用@SpringBootApplication

也可以在测试代码上使用`@SpringBootApplication`，它有这么几个好处：

1. 自身`SpringBootConfiguration`
1. 提供了`@ComponentScan`配置，以及默认的excludeFilter，有了这些filter Spring在初始化ApplicationContext的时候会排除掉某些Bean和@Configuration
1. 启用了`EnableAutoConfiguration`，这个特性能够利用Spring Boot来自动化配置所需要的外部资源，比如数据库、JMS什么的，这在集成测试的时候非常有用。

[Config][src-ex6-Config]：

```java
@SpringBootApplication(scanBasePackages = "me.chanjar.basic.service")
public class Config {
}
```

[FooServiceImpltest][src-ex6-FooServiceImpltest]：

```java
@SpringBootTest
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

## 避免@SpringBootConfiguration冲突

当`@SpringBootTest`没有定义`(classes=...`，且没有找到nested @Configuration class的情况下，会尝试查询`@SpringBootConfiguration`，如果找到多个的话则会抛出异常：

```
Caused by: java.lang.IllegalStateException: Found multiple @SpringBootConfiguration annotated classes [Generic bean: class [...]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null; defined in file [/Users/qianjia/workspace-os/spring-test-examples/basic/target/test-classes/me/chanjar/basic/springboot/ex7/FooServiceImplTest1.class], Generic bean: class [me.chanjar.basic.springboot.ex7.FooServiceImplTest2]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null; defined in file [...]]
```

比如以下代码就会造成这个问题：

```java
@SpringBootApplication(scanBasePackages = "me.chanjar.basic.service")
public class Config1 {
}

@SpringBootApplication(scanBasePackages = "me.chanjar.basic.service")
public class Config2 {
}

@SpringBootTest
public class FooServiceImplTest extends AbstractTestNGSpringContextTests {
  // ...
}
```

解决这个问题的方法有就是避免自动查询`@SpringBootConfiguration`：

1. 定义`@SpringBootTest(classes=...)`
1. 提供nested @Configuration class

## 最佳实践

除了单元测试（不需要初始化ApplicationContext的测试）外，尽量将测试配置和生产配置保持一致。比如如果生产配置里启用了AutoConfiguration，那么测试配置也应该启用。因为只有这样才能够在测试环境下发现生产环境的问题，也避免出现一些因为配置不同导致的奇怪问题。

在测试代码之间尽量做到配置共用，这么做的优点有3个：

1. 能够有效利用Spring TestContext Framework的[缓存机制][doc-context-caching]，ApplicationContext只会创建一次，后面的测试会直接用已创建的那个，加快测试代码运行速度。
1. 当项目中的Bean很多的时候，这么做能够降低测试代码复杂度，想想如果每个测试代码都有一套自己的@Configuration或其变体，那得多吓人。

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Spring TestContext Framework][doc-testcontext-framework]

[src-ex1-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex1/FooServiceImplTest.java
[src-ex2-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex2/FooServiceImplTest.java
[src-ex3-Config]: basic/src/test/java/me/chanjar/basic/springboot/ex3/Config.java
[src-ex3-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex3/FooServiceImplTest.java
[src-ex4-Config]: basic/src/test/java/me/chanjar/basic/springboot/ex4/Config.java
[src-ex4-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex4/FooServiceImplTest.java
[src-ex5-Config]: basic/src/test/java/me/chanjar/basic/springboot/ex5/Config.java
[src-ex5-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex5/FooServiceImplTest.java
[src-ex6-Config]: basic/src/test/java/me/chanjar/basic/springboot/ex6/Config.java
[src-ex6-FooServiceImpltest]: basic/src/test/java/me/chanjar/basic/springboot/ex6/FooServiceImplTest.java

[src-ex5-FooServiceImpltest1]: basic/src/test/java/me/chanjar/basic/springboot/ex5/FooServiceImplTest1.java
[src-ex5-FooServiceImpltest2]: basic/src/test/java/me/chanjar/basic/springboot/ex5/FooServiceImplTest2.java
[src-ex6-FooServiceImpltest1]: basic/src/test/java/me/chanjar/basic/springboot/ex6/FooServiceImplTest1.java
[src-ex6-FooServiceImpltest2]: basic/src/test/java/me/chanjar/basic/springboot/ex6/FooServiceImplTest2.java

[javadoc-SpringBootTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/SpringBootTest.html
[javadoc-ContextConfiguration]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/ContextConfiguration.html
[javadoc-SpringBootApplication]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/SpringBootApplication.html
[javadoc-AbstractTestNGSpringContextTests]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/testng/AbstractTestNGSpringContextTests.html
[javadoc-SpringBootApplication]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/SpringBootApplication.html
[doc-context-caching]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-ctx-management-caching
[javadoc-Configuration]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html
[javadoc-ComponentScan]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/ComponentScan.html
[javadoc-EnableAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html
[javadoc-SpringBootConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/SpringBootConfiguration.html
[doc-testcontext-framework]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-framework
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
