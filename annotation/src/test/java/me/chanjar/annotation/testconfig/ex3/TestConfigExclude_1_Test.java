package me.chanjar.annotation.testconfig.ex3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;

@SpringBootTest(classes = ExcludeConfig1.class)
public class TestConfigExclude_1_Test extends AbstractTestNGSpringContextTests {

  @Autowired(required = false)
  private TestConfig testConfig;

  @Test
  public void test() throws Exception {
    assertNull(testConfig);

  }

}
