package org.imageconverter.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerMonitor {

    private final Logger logger = LoggerFactory.getLogger(LoggerMonitor.class);

    @Before("execution(public * com.apress.cems.repos.*.JdbcPersonRepo+.findById(..)) && within(com.apress.*) ")
    public void beforeFindById(final JoinPoint joinPoint) {
	var className = joinPoint.getSignature().getDeclaringTypeName();
	var methodName = joinPoint.getSignature().getName();
	logger.info("[beforeFind]: ---> Method {}.{}  is about to be called", className, methodName);
    }

}
