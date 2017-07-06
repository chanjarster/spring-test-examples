# Chapter 0: 基本概念

在了解学习本项目提供的例子之前，先了解以下什么是单元测试（Unit Testing，简称UT)和集成测试（Integration Testing，简称IT）。

如果你之前没有深究过这两个概念，那么你可能会得出如下错误的答案：

错误答案1：

> 单元测试是测一个方法

听上去很像那么回事儿，对吧？单元测试，就是测一个逻辑单元，所以就测一个方法就是单元测试，听上去很有道理是不是？但是，那么测试两个方法的话叫什么呢？

错误答案2：

> 集成测试是把几个方法或者几个类放在一起测试

既然前面单元测试只测一个方法，那么几个方法放在一起测就是集成测试，听上去挺有道理的。

错误答案3：

> 集成测试是把系统部署到服务器上面，和其他系统联合调试做的测试

这个答案看上去很高大上，集成测试就是各个独立系统之间的的联合调试，听上去有点像SOA或者现在流行的微服务是吧。
做这种测试的时候必须得各个开发团队紧密配合，一个不小心就会测试失败，然后就是各种返工，总之难度和火箭发射有的一拼。

那么正确答案是什么？其实我也说不准，但是UT和IT具备以下特征：

1. UT和IT必须是自动化的
1. UT只专注于整个系统里的某一小部分，比如某个字符串串接方法
1. UT不需要连接外部系统，在内存里跑跑就行了
1. IT需要连接外部系统，比如针对数据库的CRUD测试

参考链接：

* [Martin Fowler - Unit Test](https://martinfowler.com/bliki/UnitTest.html)
* [Wikipedia - Unit Testing](https://en.wikipedia.org/wiki/Unit_testing)
* [Wikipedia - Integration Testing](https://en.wikipedia.org/wiki/Integration_testing)
