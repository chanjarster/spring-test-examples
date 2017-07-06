# Spring & Spring Boot Testing with TestNG的N种方法

参考文档：

* [Spring Framework Testing][doc-spring-framework-testing]
* [Spring Boot Testing][doc-spring-boot-testing]
* [Spring Guides - Testing the Web Layer][guide-testing-the-web-layer]


## 附录I Spring Mock Objects

Spring提供的[Mock Objects][doc-spring-mock-objects]有以下这些：

### package org.springframework.mock.env

1. MockEnvironment
1. MockPropertySource

### package org.springframework.mock.jndi

1. SimpleNamingContext
1. ExpectedLookupTemplate
1. SimpleNamingContextBuilder

### package org.springframework.mock.web

1. DelegatingServletInputStream
1. DelegatingServletOutputStream
1. HeaderValueHolder
1. MockAsyncContext
1. MockBodyContent
1. MockExpressionEvaluator
1. MockFilterChain
1. MockFilterConfig
1. MockHttpServletRequest
1. MockHttpServletResponse
1. MockHttpSession
1. MockJspWriter
1. MockMultipartFile
1. MockMultipartHttpServletRequest
1. MockPageContext
1. MockRequestDispatcher
1. MockServletConfig
1. MockServletContext
1. MockSessionCookieConfig
1. PassThroughFilterChain

## 附录II Spring Test Utils

Spring提供的[Unit Test Utils][doc-spring-unit-test-utils]有以下这些：

1. AopTestUtils
1. AssertionErrors
1. JsonExpectationsHelper
1. JsonPathExpectationsHelper
1. MetaAnnotationUtils
1. ReflectionTestUtils
1. XmlExpectationsHelper
1. XpathExpectationsHelper
1. ModelAndViewAssert

[doc-spring-framework-testing]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#testing
[doc-spring-boot-testing]: http://docs.spring.io/spring-boot/docs/1.5.4.RELEASE/reference/htmlsingle/#boot-features-testing
[guide-testing-the-web-layer]: https://spring.io/guides/gs/testing-web/
[doc-spring-mock-objects]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mock-objects
[doc-spring-test-utils]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#unit-testing-support-classes