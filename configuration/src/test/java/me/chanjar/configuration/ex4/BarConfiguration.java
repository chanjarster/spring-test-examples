package me.chanjar.configuration.ex4;

import me.chanjar.configuration.service.Bar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BarConfiguration.BarProperties.class)
public class BarConfiguration {

  @Autowired
  private BarProperties barProperties;

  @Bean
  public Bar bar() {
    return new Bar(barProperties.getName());
  }

  @ConfigurationProperties("bar")
  public static class BarProperties {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
