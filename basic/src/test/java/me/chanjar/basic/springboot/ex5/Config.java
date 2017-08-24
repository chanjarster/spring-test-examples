package me.chanjar.basic.springboot.ex5;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackages = "me.chanjar.basic.service")
public class Config {
}
