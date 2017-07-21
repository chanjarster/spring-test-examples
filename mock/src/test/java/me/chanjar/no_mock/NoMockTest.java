package me.chanjar.no_mock;

import me.chanjar.common.Bar;
import me.chanjar.common.FooImpl;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class NoMockTest {

  @Test
  public void testCheckCodeDuplicate1() throws Exception {

    FooImpl foo = new FooImpl();
    foo.setBar(new Bar() {
      @Override
      public Set<String> getAllCodes() {
        return Collections.singleton("123");
      }
    });
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

  @Test
  public void testCheckCodeDuplicate2() throws Exception {

    FooImpl foo = new FooImpl();
    foo.setBar(new FakeBar(Collections.singleton("123")));
    assertEquals(foo.checkCodeDuplicate("123"), true);

  }

  public class FakeBar implements Bar {

    private final Set<String> codes;

    public FakeBar(Set<String> codes) {
      this.codes = codes;
    }

    @Override
    public Set<String> getAllCodes() {
      return codes;
    }

  }

}
