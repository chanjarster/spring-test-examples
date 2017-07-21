package me.chanjar.springweb;

import me.chanjar.common.Bar;
import me.chanjar.common.FooController;
import me.chanjar.common.FooImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FooController.class)
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class SpringWeb1Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Bar bar;

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("123"));

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    when(bar.getAllCodes()).thenReturn(Collections.singleton("321"));

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

}
