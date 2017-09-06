package me.chanjar.aop.service;

import org.springframework.stereotype.Component;

@Component
public class FooServiceImpl implements FooService {

  private int count;

  @Override
  public int incrementAndGet() {
    count++;
    return count;
  }

}
