package me.chanjar.section2.boot4;

import me.chanjar.section2.service.Foo;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 读取另一个类的
 */
@SpringBootApplication(scanBasePackageClasses = Foo.class)
public interface BootConfiguration {
}
