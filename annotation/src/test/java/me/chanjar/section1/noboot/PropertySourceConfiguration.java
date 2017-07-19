package me.chanjar.section1.noboot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/me/chanjar/section1/noboot/property-source.properties")
public class PropertySourceConfiguration {
}
