package com.bootcamp.productservice.aspect;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper mapper;

    @Around("execution(* com.bootcamp.productservice.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        log.info("Incoming request -> method[{}], path: {}, class: {}.{}, starting {}ms", method, uri, className, methodName, System.currentTimeMillis() - start);



        try{
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            log.info("Outgoing response -> [{}] Success in {}ms", response.getStatus(), duration);

            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                String responseBodyJson = mapper.writeValueAsString(body);
                log.info("Response body (data only) {}", responseBodyJson);
            }

            return result;
        }catch (Exception e) {
            long duration = System.currentTimeMillis() - start;

            log.error("Outgoing error -> {} {} | Failed: {} | Time: {}ms", method, uri, e, duration);
            throw e;
        }
    }

    @Around("execution(* com.bootcamp.productservice.rest..*(..))")
    public Object logExternalApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String interfaceName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Calling External API -> {}.{}() | Args: {}", interfaceName, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            log.info("Success External API -> {}.{}() | Time: {}ms", interfaceName, methodName, duration);

            return result;
        }catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("Failed External API -> {}.{}() | Error: {} | Time: {}ms", interfaceName, methodName, e.getMessage(), duration);
            throw e;
        }
    }

    @Around("execution(* com.bootcamp.productservice.repository..*(..))")
    public Object logDatabaseQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        String repositoryName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            log.info("DB Query -> {}.{}() | Time: {}ms", repositoryName, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("DB Query Failed -> {}.{}() | Error: {} | Time: {}ms", repositoryName, methodName, e.getMessage(), duration);
            throw e;
        }
    }

    @AfterThrowing(
            pointcut = "execution(* com.bootcamp.productservice.service..*(..))",
            throwing = "ex"
    )
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception Thrown -> {}.{}() | Message: {}", className, methodName, ex.getMessage());
    }
}
