# Chapter 7: 测试@Configuration

在Spring引入Java Config机制之后，我们会越来越多的使用@Configuration来注册Bean，并且Spring Boot更广泛地使用了这一机制，其提供的大量Auto Configuration大大简化了配置工作。那么问题来了，如何确保@Configuration和Auto Configuration按照预期运行呢，是否正确地注册了Bean呢？本章举例测试@Configuration和Auto Configuration的方法（因为Auto Configuration也是@Configuration，所以测试方法是一样的）。

## 例子1：测试@Configuration

我们先写一个简单的@Configuration：

```java
@Configuration
public class FooConfiguration {

  @Bean
  public Foo foo() {
    return new Foo();
  }

}
```

然后看[FooConfiguration][src-ex1-FooConfiguration]是否能够正确地注册Bean：

```java
public class FooConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeMethod
  public void init() {
    context = new AnnotationConfigApplicationContext();
  }

  @AfterMethod(alwaysRun = true)
  public void reset() {
    context.close();
  }

  @Test
  public void testFooCreation() {
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

}
```

注意上面代码中关于Context的代码：

1. 首先，我们构造一个Context
1. 然后，注册FooConfiguration
1. 然后，refresh Context
1. 最后，在测试方法结尾close Context

如果你看Spring Boot中关于@Configuration测试的源代码会发现和上面的代码有点不一样：

```java
public class DataSourceAutoConfigurationTests {

	private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@Before
	public void init() {
		EmbeddedDatabaseConnection.override = null;
		EnvironmentTestUtils.addEnvironment(this.context,
				"spring.datasource.initialize:false",
				"spring.datasource.url:jdbc:hsqldb:mem:testdb-" + new Random().nextInt());
	}

	@After
	public void restore() {
		EmbeddedDatabaseConnection.override = null;
		this.context.close();
	}
```

这是因为Spring和Spring Boot都是用JUnit做测试的，而JUnit的特性是每次执行测试方法前，都会new一个测试类实例，而TestNG是在共享同一个测试类实例的。

## 例子2：测试@Conditional

Spring Framework提供了一种可以条件控制@Configuration的机制，即只在满足某条件的情况下才会导入@Configuration，这就是[@Conditional][doc-spring-conditonal]。

下面我们来对@Conditional做一些测试，首先我们自定义一个[Condition][javadoc-spring-condition] [FooConfiguration][src-ex2-FooConfiguration]：

```java
@Configuration
public class FooConfiguration {

  @Bean
  @Conditional(FooCondition.class)
  public Foo foo() {
    return new Foo();
  }

  public static class FooCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      if (context.getEnvironment() != null) {
        Boolean property = context.getEnvironment().getProperty("foo.create", Boolean.class);
        return Boolean.TRUE.equals(property);
      }
      return false;
    }

  }
}
```

该Condition判断[Environment][javadoc-spring-environment]中是否有`foo.create=true`。

如果我们要测试这个Condition，那么就必须往Environment里添加相关property才可以，在这里我们测试了三种情况：

1. 没有配置`foo.create=true`
1. 配置`foo.create=true`
1. 配置`foo.create=false`

[FooConfigurationTest][src-ex2-FooConfigurationTest]：

```java
public class FooConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeMethod
  public void init() {
    context = new AnnotationConfigApplicationContext();
  }

  @AfterMethod(alwaysRun = true)
  public void reset() {
    context.close();
  }

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyNull() {
    context.register(FooConfiguration.class);
    context.refresh();
    context.getBean(Foo.class);
  }

  @Test
  public void testFooCreatePropertyTrue() {
    context.getEnvironment().getPropertySources().addLast(
        new MapPropertySource("test", Collections.singletonMap("foo.create", "true"))
    );
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyFalse() {
    context.getEnvironment().getPropertySources().addLast(
        new MapPropertySource("test", Collections.singletonMap("foo.create", "false"))
    );
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

}
```

注意我们用以下方法来给Environment添加property：

```java
context.getEnvironment().getPropertySources().addLast(
  new MapPropertySource("test", Collections.singletonMap("foo.create", "true"))
);
```

所以针对@Conditional和其对应的Condition的测试的根本就是给它不一样的条件，判断其行为是否正确，在这个例子里我们的Condition比较简单，只是判断是否存在某个property，如果复杂Condition的话，测试思路也是一样的。

## 例子3：测试@ConditionalOnProperty

Spring framework只提供了@Conditional，Spring boot对这个机制做了扩展，提供了更为丰富的[@ConditionalOn*][doc-spring-boot-conditionals]，这里我们以[@ConditionalOnProperty][javadoc-spring-boot-ConditionalOnProperty]举例说明。

先看[FooConfiguration][src-ex3-FooConfiguration]：

```java
@Configuration
public class FooConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "foo", name = "create", havingValue = "true")
  public Foo foo() {
    return new Foo();
  }

}
```

[FooConfigurationTest][src-ex3-FooConfigurationTest]：

```java
public class FooConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeMethod
  public void init() {
    context = new AnnotationConfigApplicationContext();
  }

  @AfterMethod(alwaysRun = true)
  public void reset() {
    context.close();
  }

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyNull() {
    context.register(FooConfiguration.class);
    context.refresh();
    context.getBean(Foo.class);
  }

  @Test
  public void testFooCreatePropertyTrue() {
    EnvironmentTestUtils.addEnvironment(context, "foo.create=true");
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyFalse() {
    EnvironmentTestUtils.addEnvironment(context, "foo.create=false");
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

}
```

这段测试代码和例子2的逻辑差不多，只不过例子2里使用了我们自己写的Condition，这里使用了Spring Boot提供的@ConditionalOnProperty。

并且利用了Spring Boot提供的[EnvironmentTestUtils][javadoc-spring-boot-EnvironmentTestUtils]简化了给Environment添加property的工作：

```java
EnvironmentTestUtils.addEnvironment(context, "foo.create=false");
```

## 例子4：测试Configuration Properties

Spring Boot还提供了类型安全的[Configuration Properties][doc-spring-boot-config-properties]，下面举例如何对其进行测试。

[BarConfiguration][src-ex4-BarConfiguration]：

```java
@Configuration
@EnableConfigurationProperties(BarConfiguration.BarProperties.class)
public class BarConfiguration {

  @Autowired
  private BarProperties barProperties;

  @Bean
  public Bar bar() {
    return new Bar(barProperties.getName());
  }

  @ConfigurationProperties("bar")
  public static class BarProperties {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
```

[BarConfigurationTest][src-ex4-BarConfigurationTest]：

```java
public class BarConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeMethod
  public void init() {
    context = new AnnotationConfigApplicationContext();
  }

  @AfterMethod(alwaysRun = true)
  public void reset() {
    context.close();
  }

  @Test
  public void testBarCreation() {
    EnvironmentTestUtils.addEnvironment(context, "bar.name=test");
    context.register(BarConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
    context.refresh();
    assertEquals(context.getBean(Bar.class).getName(), "test");
  }

}
```

注意到因为我们使用了Configuration Properties机制，需要注册[PropertyPlaceholderAutoConfiguration][javadoc-spring-boot-property-place-holder-auto-configuration]，否则在BarConfiguration里无法注入BarProperties。

## 参考文档

* [Conditionally include @Configuration classes or @Bean methods][doc-spring-conditonal]
* [Condition annotations][doc-spring-boot-conditionals]
* [Type-safe Configuration Properties][doc-spring-boot-config-properties]   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]


[javadoc-spring-condition]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/context/annotation/Condition.html
[javadoc-spring-environment]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/core/env/Environment.html
[javadoc-spring-boot-ConditionalOnProperty]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/condition/ConditionalOnProperty.html
[javadoc-spring-boot-property-place-holder-auto-configuration]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/autoconfigure/context/PropertyPlaceholderAutoConfiguration.html
[javadoc-spring-boot-EnvironmentTestUtils]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/api/org/springframework/boot/test/util/EnvironmentTestUtils.html
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[doc-spring-boot-conditionals]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-condition-annotations
[doc-spring-conditonal]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#beans-java-conditional
[doc-spring-boot-config-properties]: https://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-external-config-typesafe-configuration-properties

[src-ex1-FooConfiguration]: configuration/src/test/java/me/chanjar/configuration/ex1/FooConfiguration.java
[src-ex1-FooConfigurationTest]: configuration/src/test/java/me/chanjar/configuration/ex1/FooConfigurationTest.java
[src-ex2-FooConfiguration]: configuration/src/test/java/me/chanjar/configuration/ex2/FooConfiguration.java
[src-ex2-FooConfigurationTest]: configuration/src/test/java/me/chanjar/configuration/ex2/FooConfigurationTest.java
[src-ex3-FooConfiguration]: configuration/src/test/java/me/chanjar/configuration/ex3/FooConfiguration.java
[src-ex3-FooConfigurationTest]: configuration/src/test/java/me/chanjar/configuration/ex3/FooConfigurationTest.java
[src-ex4-BarConfiguration]: configuration/src/test/java/me/chanjar/configuration/ex4/BarConfiguration.java
[src-ex4-BarConfigurationTest]: configuration/src/test/java/me/chanjar/configuration/ex4/BarConfigurationTest.java
