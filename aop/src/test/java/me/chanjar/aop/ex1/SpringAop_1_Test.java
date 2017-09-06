package me.chanjar.aop.ex1;

import me.chanjar.aop.config.AopConfig;
import me.chanjar.aop.service.FooService;
import me.chanjar.aop.service.FooServiceImpl;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.AopTestUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@ContextConfiguration(classes = { SpringAop_1_Test.class, AopConfig.class })
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
