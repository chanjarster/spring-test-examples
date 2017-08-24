# Chapter 1: 基本用法 - 认识TestNG

先认识一下TestNG，这里有一个[FooServiceImpl][src-FooServiceImpl]，里面有两个方法，一个是给计数器+1，一个是获取当前计数器的值：

```java
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

然后我们针对它有一个[FooServiceImplTest][src-ex1-FooServiceImplTest]作为UT：

```java
public class FooServiceImplTest {

  @Test
  public void testPlusCount() {
    FooService foo = new FooServiceImpl();
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
```

注意看代码里的`assertEquals(...)`，我们利用它来判断`Foo.getCount`方法是否按照预期执行。所以，所谓的测试其实就是给定输入、执行一些方法，assert结果是否符合预期的过程。

## 参考文档
   
* [TestNG documentation][doc-testng-doc]

[doc-testng-doc]: http://testng.org/doc/documentation-main.html
[src-FooServiceImpl]: basic/src/main/java/me/chanjar/basic/service/FooServiceImpl.java
[src-ex1-FooServiceImplTest]: basic/src/test/java/me/chanjar/basic/testng/ex1/FooServiceImplTest.java

