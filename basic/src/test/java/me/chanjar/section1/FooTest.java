package me.chanjar.section1;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FooTest {

  @Test
  public void testPlusCount() {
    Foo foo = new Foo();
    assertEquals(foo.getCount(), 0);

    foo.plusCount();
    assertEquals(foo.getCount(), 1);
  }

}
