package org.imageconverter.util.logging;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.boot.logging.LogLevel.WARN;

import java.time.Instant;
import java.util.HashMap;
import java.util.StringJoiner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggableAspect {

    @Pointcut("@annotation(org.imageconverter.util.logging.Loggable)")
    public void annotatedMethod() {
	// just to execute the Around
    }

    @Pointcut("@within(org.imageconverter.util.logging.Loggable)")
    public void annotatedClass() {
	// just to execute the Around
    }

    @Around("execution(* *(..)) && (annotatedMethod() || annotatedClass())")
    public Object log(final ProceedingJoinPoint point) throws Throwable {

	final var codeSignature = (CodeSignature) point.getSignature();
	final var methodSignature = (MethodSignature) point.getSignature();
	final var method = methodSignature.getMethod();

	final var declaringClass = method.getDeclaringClass();

	final var logger = LoggerFactory.getLogger(declaringClass);

	final var annotation = ofNullable(method.getAnnotation(Loggable.class)) //
			.orElse(declaringClass.getAnnotation(Loggable.class));

	final var level = annotation.value();
	final var unit = annotation.unit();
	final var showArgs = annotation.showArgs();
	final var showResult = annotation.showResult();
	final var showExecutionTime = annotation.showExecutionTime();

	final var methodName = method.getName();
	final var methodArgs = point.getArgs();
	final var methodParams = codeSignature.getParameterNames();

	log(logger, level, entry(methodName, showArgs, methodParams, methodArgs));

	final var start = Instant.now();

	try {

	    final var response = ofNullable(point.proceed()) //
			    .orElse("void");

	    final var end = Instant.now();

	    final var duration = format("%s %s", unit.between(start, end), unit.name().toLowerCase());

	    log(logger, level, exit(methodName, duration, response, showResult, showExecutionTime));

	    return response;

	} catch (final Exception ex) {

	    final var end = Instant.now();

	    final var duration = format("%s %s", unit.between(start, end), unit.name().toLowerCase());

	    log(logger, WARN, error(methodName, duration, ex, showExecutionTime));

	    throw ex;
	}

    }

    static String entry(final String methodName, final boolean showArgs, final String[] params, final Object[] args) {
	final var message = new StringJoiner(" ").add("Started").add(methodName).add("method");

	if (showArgs && nonNull(params) && nonNull(args) && params.length == args.length) {

	    final var values = new HashMap<String, Object>(params.length);

	    for (int i = 0; i < params.length; i++) {
		values.put(params[i], args[i]);
	    }

	    message.add("with args:").add(values.toString());
	}

	return message.toString();
    }

    static String exit(final String methodName, final String duration, final Object result, final boolean showResult, final boolean showExecutionTime) {
	final var message = new StringJoiner(" ").add("Finished").add(methodName).add("method");

	if (showExecutionTime) {
	    message.add("in").add(duration);
	}

	if (showResult) {
	    message.add("with return:").add(result.toString());
	}

	return message.toString();
    }

    static String error(final String methodName, final String duration, final Throwable ex, final boolean showExecutionTime) {

	final var message = new StringJoiner(" ").add("Finished").add(methodName).add("method").add("with").add("error").add(getRootCauseMessage(ex));

	if (showExecutionTime) {
	    message.add("in").add(duration);
	}

	return message.toString();
    }

    static void log(final Logger logger, final LogLevel level, final String message) {
	switch (level) {
	case DEBUG -> logger.debug(message);
	case TRACE -> logger.trace(message);
	case WARN -> logger.warn(message);
	case ERROR, FATAL -> logger.error(message);
	default -> logger.info(message);
	}
    }
}
