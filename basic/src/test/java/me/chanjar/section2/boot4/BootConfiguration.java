package me.chanjar.section2.boot4;

import me.chanjar.section2.service.FooService;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = FooService.class)
public interface BootConfiguration {
}
