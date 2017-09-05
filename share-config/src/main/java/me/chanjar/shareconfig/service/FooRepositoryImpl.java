package me.chanjar.shareconfig.service;

import org.springframework.jdbc.core.JdbcTemplate;

public class FooRepositoryImpl implements FooRepository {

  private JdbcTemplate jdbcTemplate;

  @Override
  public void save(Foo foo) {
    jdbcTemplate.update("INSERT INTO FOO(name) VALUES (?)", foo.getName());
  }

  @Override
  public void delete(String name) {
    jdbcTemplate.update("DELETE FROM FOO WHERE NAME = ?", name);
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

}
