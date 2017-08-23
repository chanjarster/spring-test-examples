package me.chanjar.web;

import org.springframework.stereotype.Component;

@Component
public class FooImpl implements Foo {

  @Override
  public boolean checkCodeDuplicate(String code) {
    return true;
  }

}
