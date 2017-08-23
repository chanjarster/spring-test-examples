package me.chanjar.springboot2;

import me.chanjar.web.Foo;
import me.chanjar.web.FooController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { FooController.class })
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
public class BootMvc_2_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private Foo foo;

  @Test
  public void testController() throws Exception {

    when(foo.checkCodeDuplicate(anyString())).thenReturn(true);

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
