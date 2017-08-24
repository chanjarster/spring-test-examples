package me.chanjar.annotation.overrideac.ex2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
@OverrideAutoConfiguration(enabled = false)
@SpringBootApplication
public class BootTest extends AbstractTestNGSpringContextTests {

  @Test
  public void testName() throws Exception {

  }
}
