package me.chanjar.springboot1;

import me.chanjar.domain.Foo;
import me.chanjar.domain.FooRepository;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SpringBootTest
@SpringBootApplication(scanBasePackageClasses = FooRepository.class)
public class Boot_1_IT extends AbstractTransactionalTestNGSpringContextTests {

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

}
