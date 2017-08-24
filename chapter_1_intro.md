# Chapter 1: 基本用法 - 引言

本项目所有的项目均采用Maven的标准目录结构：

* `src/main/java`，程序java文件目录
* `src/main/resource`，程序资源文件目录
* `src/test/java`，测试代码目录
* `src/test/resources`，测试资源文件目录

并且所有Maven项目都可以使用`mvn clean test`方式跑单元测试，特别需要注意，只有文件名是`*Test.java`才会被执行，一定要注意这一点哦。

## 参考文档
   
* [Maven Standard Directory Layout][doc-maven-standard-dir-layout]

[doc-maven-standard-dir-layout]: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
