package me.chanjar.annotation.testps.ex1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:me/chanjar/annotation/testps/ex1/property-source.properties")
public class PropertySourceConfig {
}
