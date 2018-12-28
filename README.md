# Spring、Spring Boot和TestNG测试指南

Spring、Spring Boot都提供了非常便利的测试工具，但遗憾的是官方文档的大多数例子都是基于JUnit的。本人比较喜欢用TestNG做单元、集成测试，所以开启了本项目收集了在Spring、Spring Boot项目中利用TestNG测试的例子。

## 章节列表

1. [Chapter 0: 基本概念][chapter_0_concept]
1. Chapter 1: 基本用法
    1. [引言][chapter_1_intro]
    1. [认识TestNG][chapter_1_s1_testng]
    1. [使用Spring Testing工具][chapter_1_s2_spring_testing]
    1. [使用Spring Boot Testing工具][chapter_1_s3_spring_boot_testing]
1. Chapter 2: Annotations
    1. [引言][chapter_2_intro]
    1. [@TestPropertySource][chapter_2_s1_test_property_source]
    1. [@ActiveProfile][chapter_2_s2_active_profile]
    1. [@JsonTest][chapter_2_s3_json_test]
    1. [@OverrideAutoConfiguration][chapter_2_s4_override_auto_configuration]
    1. [@TestConfiguration][chapter_2_s5_test_configuration]
1. [Chapter 3: 使用Mockito][chapter_3_mockito]
1. Chapter 4: 测试关系型数据库
    1. [基本做法][chapter_4_s1_basic]
    1. [使用Docker创建临时数据库][chapter_4_s2_using_docker]
1. [Chapter 5: 测试Spring MVC][chapter_5_mvc]
1. [Chapter 6: 测试AOP][chapter_6_aop]
1. [Chapter 7: 测试@Configuration][chapter_7_configuration]
1. [Chapter 8: 共享测试配置][chapter_8_share_test_config]
1. [附录I Spring Mock Objects][appendix_i]
1. [附录II Spring Test Utils][appendix_ii]


[doc-spring-test-utils]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#unit-testing-support-classes
[chapter_0_concept]: chapter_0_concept.md

[chapter_1_intro]: chapter_1_intro.md
[chapter_1_s1_testng]: chapter_1_s1_testng.md
[chapter_1_s2_spring_testing]: chapter_1_s2_spring_testing.md
[chapter_1_s3_spring_boot_testing]: chapter_1_s3_spring_boot_testing.md

[chapter_2_intro]: chapter_2_intro.md
[chapter_2_s1_test_property_source]: chapter_2_s1_test_property_source.md
[chapter_2_s2_active_profile]: chapter_2_s2_active_profile.md
[chapter_2_s3_json_test]: chapter_2_s3_json_test.md
[chapter_2_s4_override_auto_configuration]: chapter_2_s4_override_auto_configuration.md
[chapter_2_s5_test_configuration]: chapter_2_s5_test_configuration.md

[chapter_3_mockito]: chapter_3_mockito.md

[chapter_4_s1_basic]: chapter_4_s1_basic.md
[chapter_4_s2_using_docker]: chapter_4_s2_using_docker.md

[chapter_5_mvc]: chapter_5_mvc.md
[chapter_6_aop]: chapter_6_aop.md
[chapter_7_configuration]: chapter_7_configuration.md
[chapter_8_share_test_config]: chapter_8_share_test_config.md
[appendix_i]: appendix_i.md
[appendix_ii]: appendix_ii.md
