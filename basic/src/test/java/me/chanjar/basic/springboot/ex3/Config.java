package me.chanjar.basic.springboot.ex3;

import me.chanjar.basic.service.FooServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FooServiceImpl.class)
public class Config {
}
