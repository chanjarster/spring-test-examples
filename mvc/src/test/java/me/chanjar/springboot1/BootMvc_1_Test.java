package me.chanjar.springboot1;

import me.chanjar.web.FooController;
import me.chanjar.web.FooImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
public class BootMvc_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private MockMvc mvc;

  @Test
  public void testController() throws Exception {

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
