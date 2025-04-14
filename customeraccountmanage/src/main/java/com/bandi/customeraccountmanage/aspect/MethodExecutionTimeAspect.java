package com.bandi.customeraccountmanage.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExecutionTimeAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodExecutionTimeAspect.class);

    @Around("execution(* com.bandi.customeraccountmanage..*.*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("Method {}.{} executed in {} ms", className, methodName, duration);
            return result;
        } catch (Throwable e) {
            logger.error("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}