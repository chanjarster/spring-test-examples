package me.chanjar.aop.ex3;

import me.chanjar.aop.aspect.FooAspect;
import me.chanjar.aop.config.AopConfig;
import me.chanjar.aop.service.FooService;
import me.chanjar.aop.service.FooServiceImpl;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.AopTestUtils;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;

@SpringBootTest(classes = { SpringBootAopTest.class, AopConfig.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringBootAopTest extends AbstractTestNGSpringContextTests {

  @SpyBean
  private FooAspect fooAspect;

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

    verify(fooAspect, times(2)).changeIncrementAndGet(any());

  }

}
