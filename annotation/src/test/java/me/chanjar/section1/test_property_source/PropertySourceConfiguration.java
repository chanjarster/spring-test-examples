package me.chanjar.section1.test_property_source;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/me/chanjar/section1/test_property_source/property-source.properties")
public class PropertySourceConfiguration {
}
