# Chapter 6: 测试AOP

Spring提供了一套[AOP工具][doc-spring-aop]，但是当你把各种Aspect写完之后，如何确定这些Aspect都正确的应用到目标Bean上了呢？本章将举例说明如何对Spring AOP做测试。

首先先来看我们事先定义的Bean以及Aspect。

[FooServiceImpl][src-FooServiceImpl]：

```java
@Component
public class FooServiceImpl implements FooService {

  private int count;

  @Override
  public int incrementAndGet() {
    count++;
    return count;
  }

}
```

[FooAspect][src-FooAspect]：

```java
@Component
@Aspect
public class FooAspect {

  @Pointcut("execution(* me.chanjar.aop.service.FooServiceImpl.incrementAndGet())")
  public void pointcut() {
  }

  @Around("pointcut()")
  public int changeIncrementAndGet(ProceedingJoinPoint pjp) {
    return 0;
  }

}
```

可以看到`FooAspect`会修改`FooServiceImpl.incrementAndGet`方法的返回值，使其返回0。

## 例子1：测试FooService的行为

最简单的测试方法就是直接调用`FooServiceImpl.incrementAndGet`，看看它是否使用返回0。

[SpringAop_1_Test][src-ex1-SpringAop_1_Test]：

```java
@ContextConfiguration(classes = { SpringAopTest.class, AopConfig.class })
public class SpringAop_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService fooService;

  @Test
  public void testFooService() {

    assertNotEquals(fooService.getClass(), FooServiceImpl.class);

    assertTrue(AopUtils.isAopProxy(fooService));
    assertTrue(AopUtils.isCglibProxy(fooService));

    assertEquals(AopProxyUtils.ultimateTargetClass(fooService), FooServiceImpl.class);

    assertEquals(AopTestUtils.getTargetObject(fooService).getClass(), FooServiceImpl.class);
    assertEquals(AopTestUtils.getUltimateTargetObject(fooService).getClass(), FooServiceImpl.class);

    assertEquals(fooService.incrementAndGet(), 0);
    assertEquals(fooService.incrementAndGet(), 0);

  }

}
```

先看这段代码：

```java
assertNotEquals(fooService.getClass(), FooServiceImpl.class);

assertTrue(AopUtils.isAopProxy(fooService));
assertTrue(AopUtils.isCglibProxy(fooService));

assertEquals(AopProxyUtils.ultimateTargetClass(fooService), FooServiceImpl.class);

assertEquals(AopTestUtils.getTargetObject(fooService).getClass(), FooServiceImpl.class);
assertEquals(AopTestUtils.getUltimateTargetObject(fooService).getClass(), FooServiceImpl.class);
```

这些是利用Spring提供的[AopUtils][javadoc-spring-AopUtils]、[AopTestUtils][javadoc-spring-AopTestUtils]和[AopProxyUtils][javadoc-spring-AopProxyUtils]来判断`FooServiceImpl` Bean是否被代理了（Spring AOP的实现是通过[动态代理][javadoc-spring-aop-proxying]来做的）。

但是证明`FooServiceImpl` Bean被代理并不意味着`FooAspect`生效了（假设此时有多个`@Aspect`），那么我们还需要验证`FooServiceImpl.incrementAndGet`的行为：

```java
assertEquals(fooService.incrementAndGet(), 0);
assertEquals(fooService.incrementAndGet(), 0);
```

## 例子2：测试FooAspect的行为

但是总有一些时候我们是无法通过例子1的方法来测试Bean是否被正确的advised的：

1. advised方法没有返回值
1. Aspect不会修改advised方法的返回值（比如：做日志）

那么这个时候怎么测试呢？此时我们就需要用到Mockito的Spy方法结合Spring Testing工具来测试。

[SpringAop_2_Test][src-ex2-SpringAop_2_Test]：

```java
@ContextConfiguration(classes = { SpringAop_2_Test.class, AopConfig.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringAop_2_Test extends AbstractTestNGSpringContextTests {

  @SpyBean
  private FooAspect fooAspect;

  @Autowired
  private FooService fooService;

  @Test
  public void testFooService() {

    // ...
    verify(fooAspect, times(2)).changeIncrementAndGet(any());

  }

}
```

这段代码和例子1有三点区别：

1. 启用了``MockitoTestExecutionListener``，这样能够开启Mockito的支持（回顾一下[Chapter 3: 使用Mockito][chapter_3_mockito.md]）
1. ``@SpyBean private FooAspect fooAspect``，这样能够声明一个被Mockito.spy过的Bean
1. ``verify(fooAspect, times(2)).changeIncrementAndGet(any())``，使用Mockito测试``FooAspect.changeIncrementAndGet``是否被调用了两次

上面的测试代码测试的是``FooAspect``的行为，而不是``FooServiceImpl``的行为，这种测试方法更为通用。

## 例子3：Spring Boot的例子

上面两个例子使用的是Spring Testing工具，下面举例Spring Boot Testing工具如何测AOP（其实大同小异）：

[SpringBootAopTest][src-ex3-SpringBootAopTest]：

```java
@SpringBootTest(classes = { SpringBootAopTest.class, AopConfig.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringBootAopTest extends AbstractTestNGSpringContextTests {

  @SpyBean
  private FooAspect fooAspect;

  @Autowired
  private FooService fooService;

  @Test
  public void testFooService() {

    // ...
    verify(fooAspect, times(2)).changeIncrementAndGet(any());

  }

}
```

## 参考文档

* [Aspect Oriented Programming with Spring][doc-spring-aop]
* [AopUtils][javadoc-spring-AopUtils]
* [AopTestUtils][javadoc-spring-AopTestUtils]
* [AopProxyUtils][javadoc-spring-AopProxyUtils]
* [spring源码EnableAspectJAutoProxyTests][src-gh-EnableAspectJAutoProxyTests]
* [spring源码AbstractAspectJAdvisorFactoryTests][src-gh-AbstractAspectJAdvisorFactoryTests]

[chapter_3_mockito.md]: chapter_3_mockito.md
[doc-spring-aop]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/aop.html
[javadoc-spring-AopUtils]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/aop/support/AopUtils.html
[javadoc-spring-AopTestUtils]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/test/util/AopTestUtils.html
[javadoc-spring-AopProxyUtils]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/javadoc-api/org/springframework/aop/framework/AopProxyUtils.html
[javadoc-spring-aop-proxying]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/aop.html#aop-proxying

[src-FooServiceImpl]: aop/src/main/java/me/chanjar/aop/service/FooServiceImpl.java
[src-FooAspect]: aop/src/main/java/me/chanjar/aop/aspect/FooAspect.java
[src-ex1-SpringAop_1_Test]: aop/src/test/java/me/chanjar/aop/ex1/SpringAop_1_Test.java
[src-ex2-SpringAop_2_Test]: aop/src/test/java/me/chanjar/aop/ex2/SpringAop_2_Test.java
[src-ex3-SpringBootAopTest]: aop/src/test/java/me/chanjar/aop/ex3/SpringBootAopTest.java
[src-gh-EnableAspectJAutoProxyTests]: https://github.com/spring-projects/spring-framework/blob/v4.3.9.RELEASE/spring-context/src/test/java/org/springframework/context/annotation/EnableAspectJAutoProxyTests.java
[src-gh-AbstractAspectJAdvisorFactoryTests]: https://github.com/spring-projects/spring-framework/blob/v4.3.9.RELEASE/spring-aop/src/test/java/org/springframework/aop/aspectj/annotation/AbstractAspectJAdvisorFactoryTests.java
