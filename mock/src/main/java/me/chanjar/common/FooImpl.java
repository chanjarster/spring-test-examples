package me.chanjar.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FooImpl implements Foo {

  private Bar bar;

  @Override
  public boolean checkCodeDuplicate(String code) {
    return bar.getAllCodes().contains(code);
  }

  @Autowired
  public void setBar(Bar bar) {
    this.bar = bar;
  }

}
