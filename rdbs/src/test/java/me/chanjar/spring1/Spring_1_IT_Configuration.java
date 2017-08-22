package me.chanjar.spring1;

import me.chanjar.domain.FooRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackageClasses = FooRepository.class)
public class Spring_1_IT_Configuration {

  @Bean(destroyMethod = "shutdown")
  public DataSource dataSource() {

    return new EmbeddedDatabaseBuilder()
        .generateUniqueName(true)
        .setType(EmbeddedDatabaseType.H2)
        .setScriptEncoding("UTF-8")
        .ignoreFailedDrops(true)
        .addScript("classpath:me/chanjar/domain/foo-ddl.sql")
        .build();
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {

    return new JdbcTemplate(dataSource());

  }
}
