package me.chanjar.basic.springboot.ex4;

import me.chanjar.basic.service.FooServiceImpl;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import(FooServiceImpl.class)
public class Config {
}
