package me.chanjar.section2.boot6;

import me.chanjar.section2.service.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 测试类本身就是@SpringBootApplication，会启用@EnableAutoConfiguration
 */
@SpringBootTest
@OverrideAutoConfiguration(enabled = false)
@SpringBootApplication(scanBasePackageClasses = Foo.class)
public class FooImplTest_Boot6 extends AbstractTestNGSpringContextTests {

  @Autowired
  private Foo foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
