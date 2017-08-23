package me.chanjar.spring2;

import me.chanjar.common.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LooImpl implements Loo {

  private Foo foo;

  @Override
  public boolean checkCodeDuplicate(String code) {
    return foo.checkCodeDuplicate(code);
  }

  @Autowired
  public void setFoo(Foo foo) {
    this.foo = foo;
  }
}
