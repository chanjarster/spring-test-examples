package me.chanjar.configuration.ex2;

import me.chanjar.configuration.service.Foo;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

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

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyNull() {
    context.register(FooConfiguration.class);
    context.refresh();
    context.getBean(Foo.class);
  }

  @Test
  public void testFooCreatePropertyTrue() {
    context.getEnvironment().getPropertySources().addLast(
        new MapPropertySource("test", Collections.singletonMap("foo.create", "true"))
    );
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

  @Test(expectedExceptions = NoSuchBeanDefinitionException.class)
  public void testFooCreatePropertyFalse() {
    context.getEnvironment().getPropertySources().addLast(
        new MapPropertySource("test", Collections.singletonMap("foo.create", "false"))
    );
    context.register(FooConfiguration.class);
    context.refresh();
    assertNotNull(context.getBean(Foo.class));
  }

}
