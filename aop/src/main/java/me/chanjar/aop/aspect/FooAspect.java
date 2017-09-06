package me.chanjar.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class FooAspect {

  @Pointcut("execution(* me.chanjar.aop.service.FooServiceImpl.incrementAndGet())")
  public void pointcut() {
  }

  @Around("pointcut()")
  public int changeIncrementAndGet(ProceedingJoinPoint pjp) {
    return 0;
  }

}
