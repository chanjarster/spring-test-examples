package me.chanjar.section2.boot6;

import me.chanjar.section2.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 强制关闭Auto Configuration
 */
@SpringBootTest
@OverrideAutoConfiguration(enabled = false)
@SpringBootApplication(scanBasePackageClasses = FooService.class)
public class FooBoot6Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
