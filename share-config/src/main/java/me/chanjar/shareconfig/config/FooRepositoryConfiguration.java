package me.chanjar.shareconfig.config;

import me.chanjar.shareconfig.service.FooRepository;
import me.chanjar.shareconfig.service.FooRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class FooRepositoryConfiguration {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Bean
  public FooRepository fooRepository() {
    FooRepositoryImpl repository = new FooRepositoryImpl();
    repository.setJdbcTemplate(jdbcTemplate);
    return repository;
  }

}
