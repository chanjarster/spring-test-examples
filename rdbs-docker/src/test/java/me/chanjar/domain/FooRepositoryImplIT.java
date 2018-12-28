package me.chanjar.domain;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@SpringBootTest(properties = "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver")
@SpringBootApplication
public class FooRepositoryImplIT extends AbstractTransactionalTestNGSpringContextTests implements EnvironmentAware {

  @Autowired
  private FooRepository fooRepository;

  @Autowired
  private Flyway flyway;

  @Test
  public void testSave() {

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    assertEquals(countRowsInTable("FOO"), 1);
    countRowsInTableWhere("FOO", "name = 'Bob'");
  }

  @Test(dependsOnMethods = "testSave")
  public void testDelete() {

    assertEquals(countRowsInTable("FOO"), 0);

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    fooRepository.delete(foo.getName());
    assertEquals(countRowsInTable("FOO"), 0);

  }

  @AfterTest
  public void cleanDb() {
    flyway.clean();
  }

  @Override
  public void setEnvironment(Environment environment) {
    System.out.println("===================================");
    System.out.println(environment.getProperty("SPRING_DATASOURCE_URL"));
    System.out.println("===================================");
  }
}

