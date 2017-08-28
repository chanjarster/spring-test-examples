package me.chanjar.configuration.ex4;

import me.chanjar.configuration.service.Bar;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BarConfigurationTest {

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
  public void testBarCreation() {
    EnvironmentTestUtils.addEnvironment(context, "bar.name=test");
    context.register(BarConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
    context.refresh();
    assertEquals(context.getBean(Bar.class).getName(), "test");
  }

}
