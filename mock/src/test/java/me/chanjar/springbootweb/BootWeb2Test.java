package me.chanjar.springbootweb;

import me.chanjar.common.Foo;
import me.chanjar.common.FooController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@AutoConfigureMockMvc
@SpringBootTest(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class BootWeb2Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Foo foo;

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(false);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

}
