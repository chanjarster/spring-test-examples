package me.chanjar.annotation.activeprofiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Config {

  @Bean
  @Profile("dev")
  public Foo fooDev() {
    return new Foo("dev");
  }

  @Bean
  @Profile("product")
  public Foo fooProduct() {
    return new Foo("product");
  }

  @Bean
  @Profile("default")
  public Foo fooDefault() {
    return new Foo("default");
  }

  @Bean
  public Bar bar() {
    return new Bar("no profile");
  }

}
