package me.chanjar.shareconfig.service;

public interface FooRepository {
  void save(Foo foo);

  void delete(String name);
}
