# Chapter 2: Annotations - [@ActiveProfiles][doc-active-profiles]

[@ActiveProfiles][doc-active-profiles]可以用来在测试的时候启用某些Profile的Bean。本章节的测试代码使用了下面的这个配置：

```java
@Configuration
public class Config {

  @Bean
  @Profile("dev")
  public Foo fooDev() {
    return new Foo("dev");
  }

  @Bean
  @Profile("product")
  public Foo fooProduct() {
    return new Foo("product");
  }

  @Bean
  @Profile("default")
  public Foo fooDefault() {
    return new Foo("default");
  }

  @Bean
  public Bar bar() {
    return new Bar("no profile");
  }

}
```

## 例子1：不使用ActiveProfiles

在没有[@ActiveProfiles][doc-active-profiles]的时候，profile=default和没有设定profile的Bean会被加载到。

源代码[ActiveProfileTest][src-ex1-ActiveProfileTest]：

```java
@ContextConfiguration(classes = Config.class)
public class ActiveProfileTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Autowired
  private Bar bar;

  @Test
  public void test() {
    assertEquals(foo.getName(), "default");
    assertEquals(bar.getName(), "no profile");
  }

}
```

## 例子2：使用ActiveProfiles

当使用了[@ActiveProfiles][doc-active-profiles]的时候，profile匹配的和没有设定profile的Bean会被加载到。

源代码[ActiveProfileTest][src-ex2-ActiveProfileTest]：

```java
@ContextConfiguration(classes = Config.class)
[@ActiveProfiles][doc-active-profiles]("product")
public class ActiveProfileTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Autowired
  private Bar bar;

  @Test
  public void test() {
    assertEquals(foo.getName(), "product");
    assertEquals(bar.getName(), "no profile");
  }

}
```

## 总结

* 在没有[@ActiveProfiles][doc-active-profiles]的时候，profile=default和没有设定profile的Bean会被加载到。
* 当使用了[@ActiveProfiles][doc-active-profiles]的时候，profile匹配的和没有设定profile的Bean会被加载到。

[@ActiveProfiles][doc-active-profiles]同样也可以和@SpringBootTest配合使用，这里就不举例说明了。

## 参考文档
   
* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[doc-active-profiles]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#__activeprofiles
[src-ex1-ActiveProfileTest]: annotation/src/test/java/me/chanjar/annotation/activeprofiles/ex1/ActiveProfileTest.java
[src-ex2-ActiveProfileTest]: annotation/src/test/java/me/chanjar/annotation/activeprofiles/ex1/ActiveProfileTest.java 
