package me.chanjar.spring1;

import me.chanjar.domain.Foo;
import me.chanjar.domain.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = Spring_1_IT_Configuration.class)
public class Spring_1_IT extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooRepository fooRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testSave() {

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(1)
    );

  }

  @Test(dependsOnMethods = "testSave")
  public void testDelete() {

    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(1)
    );

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    fooRepository.delete(foo.getName());
    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(0)
    );
  }

}
