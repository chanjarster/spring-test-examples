package me.chanjar.section2.boot5;

import me.chanjar.section2.service.Foo;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 把 @SpringBootApplication 拆开，没有@EnableAutoConfiguration
 * Created by qianjia on 2017/7/6.
 */
@SpringBootConfiguration
@ComponentScan(
    basePackageClasses = Foo.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
    })
public interface BootConfiguration {
}
