package me.chanjar.shareconfig.configuration;

import me.chanjar.shareconfig.service.FooRepositoryTestBase;
import me.chanjar.shareconfig.testconfig.PlainConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PlainConfiguration.class)
public class FooRepositoryIT extends FooRepositoryTestBase {

}

