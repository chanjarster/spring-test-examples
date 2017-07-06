package me.chanjar.section2.boot2;

import me.chanjar.section2.service.Foo;
import me.chanjar.section2.service.FooImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 内嵌的@Configuration提供了Foo Bean
 */
@SpringBootTest
public class FooBoot2Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

  @Configuration
  public static class FooConfiguration {

    @Bean
    public Foo foo() {
      return new FooImpl();
    }

  }

}
