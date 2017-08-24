package me.chanjar.annotation.testps.ex2;

import me.chanjar.annotation.testps.ex1.PropertySourceConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

@SpringBootTest(classes = PropertySourceConfig.class)
@TestPropertySource(
    properties = { "foo=xyz", "bar=uvw", "PATH=aaa", "java.runtime.name=bbb" },
    locations = "classpath:me/chanjar/annotation/testps/ex1/test-property-source.properties"
)
public class TestPropertyTest extends AbstractTestNGSpringContextTests implements EnvironmentAware {

  private Environment environment;

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
    Map<String, Object> systemEnvironment = ((ConfigurableEnvironment) environment).getSystemEnvironment();
    System.out.println("=== System Environment ===");
    System.out.println(getMapString(systemEnvironment));
    System.out.println();

    System.out.println("=== Java System Properties ===");
    Map<String, Object> systemProperties = ((ConfigurableEnvironment) environment).getSystemProperties();
    System.out.println(getMapString(systemProperties));
  }

  @Test
  public void testOverridePropertySource() {
    assertEquals(environment.getProperty("foo"), "xyz");
  }

  @Test
  public void testOverrideSystemEnvironment() {
    assertEquals(environment.getProperty("PATH"), "aaa");
  }

  @Test
  public void testOverrideJavaSystemProperties() {
    assertEquals(environment.getProperty("java.runtime.name"), "bbb");
  }

  @Test
  public void testInlineTestPropertyOverrideResourceLocationTestProperty() {
    assertEquals(environment.getProperty("bar"), "uvw");
  }

  private String getMapString(Map<String, Object> map) {
    return String.join("\n",
        map.keySet().stream().map(k -> k + "=" + map.get(k)).collect(toList())
    );
  }
}
