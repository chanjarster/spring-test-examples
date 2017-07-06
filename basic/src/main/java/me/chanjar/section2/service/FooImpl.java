package me.chanjar.section2.service;

import org.springframework.stereotype.Component;

@Component
public class FooImpl implements Foo {

  private int count = 0;

  @Override
  public void plusCount() {
    this.count++;
  }

  @Override
  public int getCount() {
    return count;
  }

}
