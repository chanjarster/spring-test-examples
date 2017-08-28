package me.chanjar.configuration.ex1;

import me.chanjar.configuration.service.Foo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FooConfiguration {

  @Bean
  public Foo foo() {
    return new Foo();
  }

}
