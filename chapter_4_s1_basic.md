# Chapter 4: 测试关系型数据库 - 基本做法

[Spring Test Framework][doc-spring-framework-testing]提供了对[JDBC的支持][doc-spring-testing-jdbc]，能够让我们很方便对关系型数据库做集成测试。

同时Spring Boot提供了和[Flyway][site-flyway]的[集成][doc-spring-boot-flyway]支持，能够方便的管理开发过程中产生的SQL文件，配合Spring已经提供的工具能够更方便地在测试之前初始化数据库以及测试之后清空数据库。

本章节为了方便起见，本章节使用了H2作为测试数据库。

注意：**在真实的开发环境中，集成测试用数据库应该和最终的生产数据库保持一致**，这是因为不同数据库的对于SQL不是完全相互兼容的，如果不注意这一点，很有可能出现集成测试通过，但是上了生产环境却报错的问题。

因为是集成测试，所以我们使用了``maven-failsafe-plugin``来跑，它和``maven-surefire-plugin``的差别在于，``maven-failsafe-plugin``只会搜索``*IT.java``来跑测试，而``maven-surefire-plugin``只会搜索``*Test.java``来跑测试。

如果想要在maven打包的时候跳过集成测试，只需要``mvn clean install -DskipITs``。

## 被测试类

先介绍一下被测试的类。

[Foo.java][src-Foo.java]：

```java
public class Foo {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```

[FooRepositoryImpl.java][src-FooRepositoryImpl.java]：

```java
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

```

## 例子1：不使用Spring Testing提供的工具

[Spring_1_IT_Configuration.java][src-Spring_1_IT_Configuration.java]：

```java
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
```

在``Spring_1_IT_Configuration``中，我们定义了一个H2的DataSource Bean，并且构建了JdbcTemplate Bean。

注意看``addScript("classpath:me/chanjar/domain/foo-ddl.sql")``这句代码，我们让``EmbeddedDatabase``执行[foo-ddl.sql][src-foo-ddl.sql]脚本来建表：

```sql
CREATE TABLE FOO (
  name VARCHAR2(100)
);
```

[Spring_1_IT.java][src-Spring_1_IT.java]：

```java
@ContextConfiguration(classes = Spring_1_IT_Configuration.class)
public class Spring_1_IT extends AbstractTestNGSpringContextTests {

  @Autowired
  private FooRepository fooRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testSave() {

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(1)
    );

  }

  @Test(dependsOnMethods = "testSave")
  public void testDelete() {

    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(1)
    );

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    fooRepository.delete(foo.getName());
    assertEquals(
        jdbcTemplate.queryForObject("SELECT count(*) FROM FOO", Integer.class),
        Integer.valueOf(0)
    );
  }

}
```

在这段测试代码里可以看到，我们分别测试了``FooRepository``的``save``和``delete``方法，并且利用``JdbcTemplate``来验证数据库中的结果。


## 例子2：使用Spring Testing提供的工具

在这个例子里，我们会使用[JdbcTestUtils][doc-spring-testing-jdbc]来辅助测试。

[Spring_2_IT_Configuration.java][src-Spring_2_IT_Configuration.java]：

```java
@Configuration
@ComponentScan(basePackageClasses = FooRepository.class)
public class Spring_2_IT_Configuration {

  @Bean
  public DataSource dataSource() {

    EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
        .generateUniqueName(true)
        .setType(EmbeddedDatabaseType.H2)
        .setScriptEncoding("UTF-8")
        .ignoreFailedDrops(true)
        .addScript("classpath:me/chanjar/domain/foo-ddl.sql")
        .build();
    return db;
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {

    return new JdbcTemplate(dataSource());

  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

}
```

这里和例子1的区别在于，我们提供了一个``PlatformTransactionManager`` Bean，这是因为在下面的测试代码里的``AbstractTransactionalTestNGSpringContextTests``需要它。

[Spring_2_IT.java][src-Spring_2_IT.java]：

```java
@ContextConfiguration(classes = Spring_2_IT_Configuration.class)
public class Spring_2_IT extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  private FooRepository fooRepository;

  @Test
  public void testSave() {

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    assertEquals(countRowsInTable("FOO"), 1);
    countRowsInTableWhere("FOO", "name = 'Bob'");
  }

  @Test(dependsOnMethods = "testSave")
  public void testDelete() {

    assertEquals(countRowsInTable("FOO"), 0);

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    fooRepository.delete(foo.getName());
    assertEquals(countRowsInTable("FOO"), 0);

  }

}
```

在这里我们使用``countRowsInTable("FOO")``来验证数据库结果，这个方法是``AbstractTransactionalTestNGSpringContextTests``对``JdbcTestUtils``的代理。

而且要注意的是，每个测试方法在执行完毕后，会自动rollback，所以在``testDelete``的第一行里，我们``assertEquals(countRowsInTable("FOO"), 0)``，这一点和例子1里是不同的。

更多关于Spring Testing Framework与Transaction相关的信息，可以见Spring官方文档 [Transaction management][doc-spring-testing-tx]。

## 例子3：使用Spring Boot

[Boot_1_IT.java][src-Boot_1_IT.java]：

```java
@SpringBootTest
@SpringBootApplication(scanBasePackageClasses = FooRepository.class)
public class Boot_1_IT extends AbstractTransactionalTestNGSpringContextTests {

  @Autowired
  private FooRepository fooRepository;

  @Test
  public void testSave() {

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    assertEquals(countRowsInTable("FOO"), 1);
    countRowsInTableWhere("FOO", "name = 'Bob'");
  }

  @Test(dependsOnMethods = "testSave")
  public void testDelete() {

    assertEquals(countRowsInTable("FOO"), 0);

    Foo foo = new Foo();
    foo.setName("Bob");
    fooRepository.save(foo);

    fooRepository.delete(foo.getName());
    assertEquals(countRowsInTable("FOO"), 0);

  }
  
  @AfterTest
  public void cleanDb() {
    flyway.clean();
  }
  
}
```

因为使用了Spring Boot来做集成测试，得益于其AutoConfiguration机制，不需要自己构建``DataSource`` 、``JdbcTemplate``和``PlatformTransactionManager``的Bean。

并且因为我们已经将``flyway-core``添加到了maven依赖中，Spring Boot会利用flyway来帮助我们初始化数据库，我们需要做的仅仅是将sql文件放到classpath的``db/migration``目录下：

``V1.0.0__foo-ddl.sql``:
 
```sql

CREATE TABLE FOO (
  name VARCHAR2(100)
);
```

而且在测试最后，我们利用flyway清空了数据库：

```java
@AfterTest
public void cleanDb() {
  flyway.clean();
}
```

使用flyway有很多好处：

1. 每个sql文件名都规定了版本号
1. flyway按照版本号顺序执行
1. 在开发期间，只需要将sql文件放到db/migration目录下就可以了，不需要写类似``EmbeddedDatabaseBuilder.addScript()``这样的代码
1. 基于以上三点，就能够将数据库初始化SQL语句也纳入到集成测试中来，保证代码配套的SQL语句的正确性
1. 可以帮助你清空数据库，这在你使用非内存数据库的时候非常有用，因为不管测试前还是测试后，你都需要一个干净的数据库

## 参考文档

本章节涉及到的Spring Testing Framework JDBC、SQL相关的工具：

* [Transaction management][doc-spring-testing-tx]
* [Executing SQL scripts][doc-spring-testing-sql]

和flyway相关的：

* [flyway的官方文档][doc-flyway]
* [flway和spring boot的集成][doc-spring-boot-flyway]

[doc-spring-boot-flyway]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup
[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[doc-spring-testing-jdbc]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#integration-testing-support-jdbc
[doc-spring-boot-flyway]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup
[site-flyway]: https://flywaydb.org/
[doc-flyway]: https://flywaydb.org/documentation/
[doc-spring-testing-tx]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testcontext-tx
[doc-spring-testing-sql]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#testcontext-executing-sql
[src-Foo.java]: rdbs/src/main/java/me/chanjar/domain/Foo.java
[src-FooRepositoryImpl.java]: rdbs/src/main/java/me/chanjar/domain/FooRepositoryImpl.java
[src-foo-ddl.sql]: rdbs/src/main/resources/me/chanjar/domain/foo-ddl.sql
[src-Spring_1_IT.java]: rdbs/src/test/java/me/chanjar/spring1/Spring_1_IT.java
[src-Spring_1_IT_Configuration.java]: rdbs/src/test/java/me/chanjar/spring1/Spring_1_IT_Configuration.java
[src-Spring_2_IT.java]: rdbs/src/test/java/me/chanjar/spring2/Spring_2_IT.java
[src-Spring_2_IT_Configuration.java]: rdbs/src/test/java/me/chanjar/spring2/Spring_2_IT_Configuration.java
[src-Boot_1_IT.java]: rdbs/src/test/java/me/chanjar/springboot1/Boot_1_IT.java
