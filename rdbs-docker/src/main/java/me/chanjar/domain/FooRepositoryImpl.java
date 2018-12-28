package me.chanjar.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
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

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

}
