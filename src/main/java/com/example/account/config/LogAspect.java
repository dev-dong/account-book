package com.example.account.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* com.example.account.service.*.*(..))")
    public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Executing service method: {}", methodName);
        return joinPoint.proceed();
    }
}
