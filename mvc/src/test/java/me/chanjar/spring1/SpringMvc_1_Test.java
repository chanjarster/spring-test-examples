package me.chanjar.spring1;

import me.chanjar.web.FooController;
import me.chanjar.web.FooImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@EnableWebMvc
@WebAppConfiguration
@ContextConfiguration(classes = { FooController.class, FooImpl.class })
public class SpringMvc_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mvc;

  @BeforeMethod
  public void prepareMockMvc() {
    this.mvc = webAppContextSetup(wac).build();
  }

  @Test
  public void testController() throws Exception {

    this.mvc.perform(get("/foo/check-code-dup").param("code", "123"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

  }

}
