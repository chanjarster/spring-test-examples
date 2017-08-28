package me.chanjar.configuration.ex2;

import me.chanjar.configuration.service.Foo;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
public class FooConfiguration {

  @Bean
  @Conditional(FooCondition.class)
  public Foo foo() {
    return new Foo();
  }

  public static class FooCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      if (context.getEnvironment() != null) {
        Boolean property = context.getEnvironment().getProperty("foo.create", Boolean.class);
        return Boolean.TRUE.equals(property);
      }
      return false;
    }

  }
}
