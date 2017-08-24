package me.chanjar.basic.testng.ex1;

import me.chanjar.basic.service.FooService;
import me.chanjar.basic.service.FooServiceImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FooServiceImplTest {

  @Test
  public void testPlusCount() {
    FooService foo = new FooServiceImpl();
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
