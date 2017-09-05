package me.chanjar.shareconfig.annotation;

import me.chanjar.shareconfig.service.FooRepositoryTestBase;
import me.chanjar.shareconfig.testconfig.AnnotationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FooRepositoryIT.class)
@AnnotationConfiguration
public class FooRepositoryIT extends FooRepositoryTestBase {

}

