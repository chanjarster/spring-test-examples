package me.chanjar.section2.boot3;

import me.chanjar.section2.service.Foo;
import me.chanjar.section2.service.FooImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by qianjia on 2017/7/6.
 */
@Configuration
public class FooConfiguration {

  @Bean
  public Foo foo() {
    return new FooImpl();
  }

}
