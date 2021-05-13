package com.hocztms.config;

import com.hocztms.common.RestResult;
import com.hocztms.springSecurity.jwt.JwtAuthService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;


@Aspect
@Component
public class WebLogAspect {

    @Autowired
    private JwtAuthService jwtAuthService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(* com.hocztms.controller.*.*(..))")
    private void weblogPointcut(){

    }

    @Before("weblogPointcut()")
    private void doBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URL: "+request.getRequestURL().toString()+" HTTP_METHOD: "+request.getMethod()+" IP: "+request.getRemoteAddr()+" User: " + jwtAuthService.getTokenUsername(request));
        logger.info("data: "+ Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "result", pointcut = "weblogPointcut()")
    public void doAfterReturning(Object result) throws Throwable {
        logger.info("RESPONSE: " + result);
    }

}
