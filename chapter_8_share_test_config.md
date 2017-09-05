# Chapter 8: 共享测试配置

在[使用Spring Boot Testing工具][chapter_1_s3_spring_boot_testing]中提到：

> 在测试代码之间尽量做到配置共用。
> ...
> 能够有效利用Spring TestContext Framework的[缓存机制][doc-context-caching]，ApplicationContext只会创建一次，后面的测试会直接用已创建的那个，加快测试代码运行速度。

本章将列举几种共享测试配置的方法

## @Configuration

我们可以将测试配置放在一个@Configuration里，然后在测试@SpringBootTest或ContextConfiguration中引用它。


[PlainConfiguration][src-ex1-PlainConfiguration.java]：

```java
@SpringBootApplication(scanBasePackages = "me.chanjar.shareconfig")
public class PlainConfiguration {
}
```

[FooRepositoryIT][src-ex1-FooRepositoryIT.java]：

```java
@SpringBootTest(classes = PlainConfiguration.class)
public class FooRepositoryIT extends ...
```

## @Configuration on interface

也可以把@Configuration放到一个interface上。

[PlainConfiguration][src-ex2-InterfaceConfiguration.java]：

```java
@SpringBootApplication(scanBasePackages = "me.chanjar.shareconfig")
public interface InterfaceConfiguration {
}
```

[FooRepositoryIT][src-ex2-FooRepositoryIT.java]：

```java
@SpringBootTest(classes = InterfaceConfiguration.class)
public class FooRepositoryIT extends ...
```
## Annotation

也可以利用Spring的[Meta-annotations][doc-spring-meta-annotations]及[自定义机制][doc-spring-annotation-programming-model]，提供自己的Annotation用在测试配置上。

[PlainConfiguration][src-ex3-AnnotationConfiguration.java]：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(scanBasePackages = "me.chanjar.shareconfig")
public @interface AnnotationConfiguration {
}
```

[FooRepositoryIT][src-ex3-FooRepositoryIT.java]：

```java
@SpringBootTest(classes = FooRepositoryIT.class)
@AnnotationConfiguration
public class FooRepositoryIT extends ...
```
## 参考文档

* [Meta-annotations][doc-spring-meta-annotations]
* [Meta-Annotation Support for Testing][doc-spring-meta-annotations-for-testing]
* [Spring Annotation Programming Model][doc-spring-annotation-programming-model]

[chapter_1_s3_spring_boot_testing]: chapter_1_s3_spring_boot_testing.md
[doc-context-caching]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#testcontext-ctx-management-caching
[doc-spring-meta-annotations-for-testing]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/integration-testing.html#integration-testing-annotations-meta
[doc-spring-meta-annotations]: https://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/html/beans.html#beans-meta-annotations
[doc-spring-annotation-programming-model]: https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model
[src-ex1-PlainConfiguration.java]: share-config/src/test/java/me/chanjar/shareconfig/testconfig/PlainConfiguration.java
[src-ex1-FooRepositoryIT.java]: share-config/src/test/java/me/chanjar/shareconfig/configuration/FooRepositoryIT.java
[src-ex2-InterfaceConfiguration.java]: share-config/src/test/java/me/chanjar/shareconfig/testconfig/InterfaceConfiguration.java
[src-ex2-FooRepositoryIT.java]: share-config/src/test/java/me/chanjar/shareconfig/inter/FooRepositoryIT.java
[src-ex3-AnnotationConfiguration.java]: share-config/src/test/java/me/chanjar/shareconfig/annotation/FooRepositoryIT.java
[src-ex3-FooRepositoryIT.java]: share-config/src/test/java/me/chanjar/shareconfig/testconfig/AnnotationConfiguration.java
