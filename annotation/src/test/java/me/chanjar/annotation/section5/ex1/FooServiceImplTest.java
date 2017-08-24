package me.chanjar.annotation.section5.ex1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
@SpringBootConfiguration
@Import(TestConfig.class)
public class FooServiceImplTest extends AbstractTestNGSpringContextTests {

  @Autowired

  @Test
  public void testPlusCount() throws Exception {
    // 如果存在 @TestConfiguration 那么在其他测试代码里必须使用 @SpringBootApplication or @ComponentScan(excludeFilters = TypeExcludeFilter...)
    // 否则会被扫描到，扫描到就不好了
  }

}
