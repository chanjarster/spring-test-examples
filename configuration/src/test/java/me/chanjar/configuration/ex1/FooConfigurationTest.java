package me.chanjar.configuration.ex1;

import me.chanjar.configuration.service.Foo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class FooConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeMethod
  public void init() {
    context = new AnnotationConfigApplicationContext();
  }

  @AfterMethod(alwaysRun = true)
  public void reset() {
    context.close();
  }

  @Test
  public void testFooCreation() {
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

}
