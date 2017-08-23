package me.chanjar.spring2;

import me.chanjar.common.Bar;
import me.chanjar.common.FooImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = { FooImpl.class, LooImpl.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class Spring_2_Test extends AbstractTestNGSpringContextTests {

  @MockBean
  private Bar bar;

  @Autowired
  private Loo loo;

  @Test
  public void testCheckCodeDuplicate() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));
    assertEquals(loo.checkCodeDuplicate("123"), true);

  }

}
