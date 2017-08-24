package me.chanjar.annotation.overrideac.ex1;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
@SpringBootApplication
public class BootTest extends AbstractTestNGSpringContextTests {

  @Test
  public void testName() throws Exception {

  }
}
