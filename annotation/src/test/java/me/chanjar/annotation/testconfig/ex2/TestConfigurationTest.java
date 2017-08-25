package me.chanjar.annotation.testconfig.ex2;

import me.chanjar.annotation.testconfig.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = { Config.class, TestConfig.class })
public class TestConfigurationTest extends AbstractTestNGSpringContextTests {

  @Qualifier("foo")
  @Autowired
  private Foo foo;

  @Qualifier("foo2")
  @Autowired
  private Foo foo2;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getName(), "from test config");
    assertEquals(foo2.getName(), "from test config2");

  }

}
