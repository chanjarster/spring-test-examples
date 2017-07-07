package me.chanjar.section2.boot3;

import me.chanjar.section2.service.FooService;
import me.chanjar.section2.service.FooServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FooConfiguration {

  @Bean
  public FooService foo() {
    return new FooServiceImpl();
  }

}
