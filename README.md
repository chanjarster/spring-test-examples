# Testing Spring Boot with TestNG系列文章

本人比较喜欢用TestNG做单元、集成测试，所以本文的例子都是基于TestNG的。
而且Spring & Spring Boot官方对于Testing的例子大多是基于JUnit的，所以本文也能够给TestNG爱好者提供一些有用的帮助。

## 章节列表

1. [Chapter 0: 基本概念][chapter_0_concept]
1. [Chapter 1: 基本用法][chapter_1_basic]
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
1. [Chapter 3: 使用Mockito][chapter_3_mockito]
1. [Chapter 4: 测试关系型数据库][chapter_4_rdbs]
1. [Chapter 5: 测试Spring MVC][chapter_5_mvc]
1. Chapter 6: 测试AOP
1. [Chapter 7: 测试@Configuration][chapter_6_configuration]
1. [Chapter 8: 测试@AutoConfiguration][chapter_7_auto_configuration]
1. [附录I Spring Mock Objects][appendix_i]
1. [附录II Spring Test Utils][appendix_ii]


[doc-spring-test-utils]: http://docs.spring.io/spring/docs/4.3.9.RELEASE/spring-framework-reference/htmlsingle/#unit-testing-support-classes
[chapter_0_concept]: chapter_0_concept.md

[chapter_1_basic]: chapter_1_basic.md
[chapter_1_intro]: chapter_1_intro.md
[chapter_1_s1_testng]: chapter_1_s1_testng.md
[chapter_1_s2_spring_testing]: chapter_1_s1_spring_testing.md
[chapter_1_s3_spring_boot_testing]: chapter_1_s3_spring_boot_testing.md

[chapter_2_intro]: chapter_2_intro.md
[chapter_2_s1_test_property_source]: chapter_2_s1_test_property_source.md
[chapter_2_s2_active_profile]: chapter_2_s2_active_profile.md
[chapter_2_s3_json_test]: chapter_2_s3_json_test.md
[chapter_2_s4_override_auto_configuration]: chapter_2_s4_override_auto_configuration.md

[chapter_3_mockito]: chapter_3_mockito.md
[chapter_4_rdbs]: chapter_4_rdbs.md
[chapter_5_mvc]: chapter_5_mvc.md
[chapter_6_configuration]: chapter_6_configuration.md
[chapter_7_auto_configuration]: chapter_7_auto_configuration.md
[appendix_i]: appendix_i.md
[appendix_ii]: appendix_ii.md
