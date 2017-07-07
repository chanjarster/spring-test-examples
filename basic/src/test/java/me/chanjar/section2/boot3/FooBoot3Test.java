package me.chanjar.section2.boot3;

import me.chanjar.section2.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 使用外部Configuration加载Bean
 */
@SpringBootTest(classes = FooConfiguration.class)
public class FooBoot3Test extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooService foo;

  @Test
  public void testPlusCount() throws Exception {
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
