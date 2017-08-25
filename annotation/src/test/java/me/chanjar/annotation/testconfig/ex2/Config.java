package me.chanjar.annotation.testconfig.ex2;

import me.chanjar.annotation.testconfig.Foo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public Foo foo() {
    return new Foo("from config");
  }
}
