package me.chanjar.annotation.jsontest.ex1;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by qianjia on 2017/7/18.
 */
public class Foo {

  private String name;

  private int age;

  public Foo() {
  }

  public Foo(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Foo foo = (Foo) o;

    if (age != foo.age) return false;
    return name != null ? name.equals(foo.name) : foo.name == null;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + age;
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", name)
        .append("age", age)
        .toString();
  }
}
