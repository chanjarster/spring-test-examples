package me.chanjar.annotation.testconfig.ex1;

import me.chanjar.annotation.testconfig.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SpringBootTest
@SpringBootConfiguration
public class TestConfigurationTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getName(), "from test config");
  }

  @TestConfiguration
  public class TestConfig {

    @Bean
    public Foo foo() {
      return new Foo("from test config");
    }

  }
}
