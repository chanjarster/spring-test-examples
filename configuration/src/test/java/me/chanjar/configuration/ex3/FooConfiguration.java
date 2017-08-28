package me.chanjar.configuration.ex3;

import me.chanjar.configuration.service.Foo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FooConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "foo", name = "create", havingValue = "true")
  public Foo foo() {
    return new Foo();
  }

}
