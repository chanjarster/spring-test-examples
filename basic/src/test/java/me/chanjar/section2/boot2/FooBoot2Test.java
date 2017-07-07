package me.chanjar.section2.boot2;

import me.chanjar.section2.service.FooService;
import me.chanjar.section2.service.FooServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 使用内嵌@Configuration加载Bean
 */
@SpringBootTest
public class FooBoot2Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

  @Configuration
  public static class FooConfiguration {

    @Bean
    public FooService foo() {
      return new FooServiceImpl();
    }

  }

}
