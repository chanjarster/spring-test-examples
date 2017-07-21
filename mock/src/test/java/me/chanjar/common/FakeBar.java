package me.chanjar.common;

import java.util.Set;

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
