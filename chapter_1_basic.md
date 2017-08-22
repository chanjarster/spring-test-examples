# Chapter 1: 基本用法

本项目均可以使用`mvn clean test`方式跑单元测试，特别需要注意，只有文件名是`*Test.java`才会被执行，一定要注意这一点哦。

## Section 1: TestNG

先认识一下TestNG，这里有一个[Foo][src-Foo]类，里面有两个方法，一个是给计数器+1，一个是获取当前计数器的值：

```java
public class Foo {

  private int count = 0;

  public void plusCount() {
    this.count++;
  }

  public int getCount() {
    return count;
  }

}
```

然后我们针对它有一个[FooTest][src-FooTest]作为UT：

```java
public class FooTest {

  @Test
  public void testPlusCount() {
    Foo foo = new Foo();
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
```

看上去挺简单不是？记住，所谓的测试其实就是给定输入、执行一些方法，assert结果是否符合预期的过程。

## Section 2: Spring Boot Testing with TestNG

前面我们使用了TestNG做了最简单的UT，那么在Spring Boot项目里，如何针对Bean做测试呢？其实你也可以像前面一样测，但是如果你需要利用Spring提供的依赖注入特性来做测试，该怎么做？

为了说明Spring Boot下的TestNG测试，我们先提供了[FooService][src-FooService]及其实现[FooServiceImpl][src-FooServiceImpl]：

```java
public interface FooService {

  void plusCount();

  int getCount();

}

@Component
public class FooServiceImpl implements FooService {

  private int count = 0;

  @Override
  public void plusCount() {
    this.count++;
  }

  @Override
  public int getCount() {
    return count;
  }

}
```

注意在这里我们把[FooServiceImpl][src-FooServiceImpl]注册为了一个Bean。

### 方式1：使用@SpringBootApplication加载Bean

源代码见[FooBoot1Test][src-FooBoot1Test]：

```java
@SpringBootTest
@SpringBootApplication(scanBasePackageClasses = FooService.class)
public class FooBoot1Test extends AbstractTestNGSpringContextTests {

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

为了能够使用TestNG测试Spring代码，必须将测试类继承[AbstractTestNGSpringContextTests][javadoc-AbstractTestNGSpringContextTests]。

在这里，我们把测试类变成了[@SpringBootApplication][javadoc-SpringBootApplication]，它使用了自动扫描机制`scanBasePackageClasses`来加载Bean。并且标注了[@SpringBootTest][javadoc-SpringBootTest]来说明这是一个Spring Boot Test。

[@SpringBootTest][javadoc-SpringBootTest]的内在机制是：在没有内嵌[@Configuration][javadoc-Configuration]，且没有指定`@SpringBootTest(classes=...)`的时候，会去尝试查找[@SpringBootConfiguration][javadoc-SpringBootConfiguration]，而[@SpringBootApplication][javadoc-SpringBootApplication]就是[@SpringBootConfiguration][javadoc-SpringBootConfiguration]。需要注意的是，同一个JVM中只能有一个[@SpringBootConfiguration][javadoc-SpringBootConfiguration]，如果有多个就启动不起来了。

[@SpringBootApplication][javadoc-SpringBootApplication]相比[@SpringBootConfiguration][javadoc-SpringBootConfiguration]多了些什么呢？我们看看其源码就会知道：

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
		@Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
  // ...
}
```

[@SpringBootApplication][javadoc-SpringBootApplication]多了[@ComponentScan][javadoc-ComponentScan]的配置，以及[@EnableAutoConfiguration][javadoc-EnableAutoConfiguration]，知道这一点很有用，因为如果你想限定UT所加载的Bean范围，就可以利用这点。

事实上你在运行本Section的例子的时候会发现有的例子里会打印出下面这条日志，有的则不会：

```
2017-07-07 13:44:37.537  INFO 14399 --- [           main] m.c.a.AutoConfigurationEnableLogger      : Auto Configuration Enabled
```

带着这个问题接着往后看吧。

### 方式2：使用内嵌@Configuration加载Bean

源代码见[FooBoot2Test][src-FooBoot2Test]：

```java
@SpringBootTest
public class FooBoot2Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

  @Configuration
  public static class FooConfiguration {

    @Bean
    public FooService foo() {
      return new FooServiceImpl();
    }

  }

}
```

在这里我们在[@SpringBootTest][javadoc-SpringBootTest]测试类里内嵌了[@Configuration][javadoc-Configuration]，和方式1不同的是，方式2没有使用自动扫描机制，而是使用内嵌[@Configuration][javadoc-Configuration]注册了Bean。

### 方式3：使用外部@Configuration加载Bean

源代码见[FooBoot3Test][src-FooBoot3Test]和[FooConfiguration][src-FooConfiguration]：

```java
@SpringBootTest(classes = FooConfiguration.class)
public class FooBoot3Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}

@Configuration
public class FooConfiguration {

  @Bean
  public FooService foo() {
    return new FooServiceImpl();
  }

}
```

这个方式和方式2差不多，只不过将[@Configuration][javadoc-Configuration]放到了外部。

### 方式4：使用外部@SpringBootApplication加载Bean

源代码见[FooBoot4Test][src-FooBoot4Test]和[BootConfiguration][src-4-BootConfiguration]：

```java
@SpringBootTest(classes = BootConfiguration.class)
public class FooBoot4Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}

@SpringBootApplication(scanBasePackageClasses = FooService.class)
public interface BootConfiguration {
}
```

### 方式5：使用外部@SpringBootConfiguration加载Bean

源代码见[FooBoot5Test][src-FooBoot5Test]和[BootConfiguration][src-5-BootConfiguration]：

```java
@SpringBootTest(classes = BootConfiguration.class)
public class FooBoot5Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}

@SpringBootConfiguration
@ComponentScan(
    basePackageClasses = FooService.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
    })
public interface BootConfiguration {
}
```

### 方式6：关闭Auto Configuration

源代码见[FooBoot6Test][src-FooBoot6Test]

```java
@SpringBootTest
@OverrideAutoConfiguration(enabled = false)
@SpringBootApplication(scanBasePackageClasses = FooService.class)
public class FooBoot6Test extends AbstractTestNGSpringContextTests {

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

在这里我们使用了[@OverrideAutoConfiguration][javadoc-OverrideAutoConfiguration]关闭了Auto Configuration。

## Section 3: Spring Testing with TestNG

这个部分我们会讲Old School测试Spring项目的方式，即不使用Spring Boot的脚手架来做测试。

### 方式1：使用SpringBootTestContextBootstrapper

源代码见[FooNoBoot1Test][src-FooNoBoot1Test]：

```java
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ContextConfiguration(classes = FooServiceImpl.class)
public class FooNoBoot1Test extends AbstractTestNGSpringContextTests {

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

在这里我们使用了[SpringBootTestContextBootstrapper][javadoc-SpringBootTestContextBootstrapper]，其实[@SpringBootTest][javadoc-SpringBootTest]使用的就是[SpringBootTestContextBootstrapper][javadoc-SpringBootTestContextBootstrapper]，所以我们这里模拟了[@SpringBootTest][javadoc-SpringBootTest]的行为。

同时我们也使用了[@ContextConfiguration][javadoc-ContextConfiguration]来加载Bean。

### 方式2：传统方式

源代码见[FooNoBoot2Test][src-FooNoBoot2Test]：


```java
@ContextConfiguration(classes = FooServiceImpl.class)
public class FooNoBoot2Test extends AbstractTestNGSpringContextTests {

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

这是我们目前为止看到的最少的测试代码，和方式1不同，在这个例子里[Spring TestContext Framework][doc-testcontext-framework]实际上使用的是[DefaultTestContextBootstrapper][javadoc-DefaultTestContextBootstrapper]。

PS. 对比一下和方式1的日志输出，看看有什么不一样。

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]

[src-Foo]: basic/src/main/java/me/chanjar/section1/Foo.java
[src-FooTest]: basic/src/test/java/me/chanjar/section1/FooTest.java
[src-FooService]: basic/src/main/java/me/chanjar/section2/service/FooService.java
[src-FooServiceImpl]: basic/src/main/java/me/chanjar/section2/service/FooServiceImpl.java
[src-FooBoot1Test]: basic/src/test/java/me/chanjar/section2/boot1/FooBoot1Test.java
[src-FooBoot2Test]: basic/src/test/java/me/chanjar/section2/boot2/FooBoot2Test.java
[src-FooBoot3Test]: basic/src/test/java/me/chanjar/section2/boot3/FooBoot3Test.java
[src-FooConfiguration]: basic/src/test/java/me/chanjar/section2/boot3/FooConfiguration.java
[src-FooBoot4Test]: basic/src/test/java/me/chanjar/section2/boot4/FooBoot4Test.java
[src-4-BootConfiguration]: basic/src/test/java/me/chanjar/section2/boot4/BootConfiguration.java
[src-FooBoot5Test]: basic/src/test/java/me/chanjar/section2/boot5/FooBoot5Test.java
[src-5-BootConfiguration]: basic/src/test/java/me/chanjar/section2/boot5/BootConfiguration.java
[src-FooBoot6Test]: basic/src/test/java/me/chanjar/section2/boot6/FooBoot6Test.java
[src-FooNoBoot1Test]: basic/src/test/java/me/chanjar/section3/noboot1/FooNoBoot1Test.java
[src-FooNoBoot2Test]: basic/src/test/java/me/chanjar/section3/noboot2/FooNoBoot2Test.java
[javadoc-SpringBootApplication]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/SpringBootApplication.html
[javadoc-AbstractTestNGSpringContextTests]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/testng/AbstractTestNGSpringContextTests.html
[javadoc-SpringBootApplication]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/SpringBootApplication.html
[javadoc-SpringBootTest]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/SpringBootTest.html
[javadoc-SpringBootConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/SpringBootConfiguration.html
[javadoc-Configuration]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html
[javadoc-ComponentScan]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/ComponentScan.html
[javadoc-EnableAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html
[javadoc-OverrideAutoConfiguration]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/autoconfigure/OverrideAutoConfiguration.html
[javadoc-DefaultTestContextBootstrapper]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/support/DefaultTestContextBootstrapper.html
[javadoc-SpringBootTestContextBootstrapper]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/context/SpringBootTestContextBootstrapper.html
[doc-testcontext-framework]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-framework
[javadoc-ContextConfiguration]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/context/ContextConfiguration.html
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
