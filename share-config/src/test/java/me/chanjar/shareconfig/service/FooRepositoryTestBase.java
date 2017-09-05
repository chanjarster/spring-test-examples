package me.chanjar.shareconfig.service;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FooRepositoryTestBase extends AbstractTransactionalTestNGSpringContextTests {

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


