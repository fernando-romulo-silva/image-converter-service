package org.imageconverter.util.logging;

import static java.lang.Long.MAX_VALUE;
import static java.lang.String.format;
import static java.util.Locale.ROOT;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.boot.logging.LogLevel.DEBUG;
import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.FATAL;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.boot.logging.LogLevel.OFF;
import static org.springframework.boot.logging.LogLevel.TRACE;
import static org.springframework.boot.logging.LogLevel.WARN;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.Range;
import org.apache.commons.text.StringEscapeUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.security.util.FieldUtils;
import org.springframework.stereotype.Component;

/**
 * The Aspect to log the method enters and method exists.
 * 
 * @author Fernando Romulo da Silva
 */
@Aspect
@Component
public class LoggableAspect {

    /**
     * The pointcut to annotated class.
     */
    @Pointcut("@annotation(org.imageconverter.util.logging.Loggable)")
    public void annotatedMethod() {
	// just to execute the Around
    }

    /**
     * The pointcut to annotated method.
     */
    @Pointcut("@within(org.imageconverter.util.logging.Loggable)")
    public void annotatedClass() {
	// just to execute the Around
    }

    /**
     * Execute the wrap method.
     * 
     * @param point The object that support around advice in @AJ aspects
     * @return The methot's return
     * @throws Throwable If something wrong happens
     */
    @Around("execution(* *(..)) && (annotatedMethod() || annotatedClass())")
    public Object log(final ProceedingJoinPoint point) throws Throwable {

	final var codeSignature = (CodeSignature) point.getSignature();
	final var methodSignature = (MethodSignature) point.getSignature();
	final var method = methodSignature.getMethod();

	final var declaringClass = method.getDeclaringClass();
	
	final var target = point.getTarget();

	final var logger = getLogger(declaringClass, target);

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

	executeLog(logger, level, entry(methodName, showArgs, methodParams, methodArgs));

	final var start = Instant.now();

	final var lowerCaseUnit = unit.name().toLowerCase(ROOT);

	try {

	    final var response = ofNullable(point.proceed()) //
			    .orElse("void");

	    final var end = Instant.now();

	    final var duration = format("%s %s", unit.between(start, end), lowerCaseUnit);

	    executeLog(logger, level, exit(methodName, duration, response, showResult, showExecutionTime));

	    return response;

	} catch (final Exception ex) { // NOPMD - point.proceed() throw it

	    final var end = Instant.now();

	    final var duration = format("%s %s", unit.between(start, end), lowerCaseUnit);

	    executeLog(logger, WARN, error(methodName, duration, ex, showExecutionTime));

	    throw ex;
	}

    }

    private String entry(final String methodName, final boolean showArgs, final String[] params, final Object... args) {
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

    private String exit(final String methodName, final String duration, final Object result, final boolean showResult, final boolean showExecutionTime) {
	final var message = new StringJoiner(" ").add("Finished").add(methodName).add("method");

	if (showExecutionTime) {
	    message.add("in").add(duration);
	}

	if (showResult) {
	    message.add("with return:").add(result.toString());
	}

	return message.toString();
    }

    private String error(final String methodName, final String duration, final Throwable ex, final boolean showExecutionTime) {

	final var message = new StringJoiner(" ").add("Finished").add(methodName).add("method").add("with").add("error").add(getRootCauseMessage(ex));

	if (showExecutionTime) {
	    message.add("in").add(duration);
	}

	return message.toString();
    }

    private void executeLog(final Logger logger, final LogLevel level, final String message) {
	
	if (OFF.equals(level)) {
	    return;
	}
	
	final var finalMsg = StringEscapeUtils.escapeJava(message);
	
	switch (level) { // NOPMD - SwitchStmtsShouldHaveDefault: Actually we have a 'default'
		case TRACE -> logger.trace(finalMsg);
		case DEBUG -> logger.debug(finalMsg);
		case INFO -> logger.info(finalMsg);
		case WARN -> logger.warn(finalMsg);
		case ERROR, FATAL -> logger.error(finalMsg);
		default -> logger.debug(finalMsg);
	}
    }
    
    private Logger getLogger(final Class<?> clazz, final Object object) {
	
	try {
	    
	    final var fields = clazz.getDeclaredFields();
			    
	    final var fieldOptional = Arrays.stream(fields)
			    .map(Field::getName)
			    .filter(field -> containsAny(field, "logger", "LOGGER"))
			    .findFirst();
	    
	    if (fieldOptional.isEmpty()) {
		return LoggerFactory.getLogger(clazz);
	    }
	    
	    return (Logger) FieldUtils.getFieldValue(object, fieldOptional.get());
	    
	} catch (final Exception ex) {
	    return LoggerFactory.getLogger(clazz);
	}
    }
    
    public LogLevel getLevel(final Instant start, final Instant end) {
	
	final var unit = ChronoUnit.MILLIS;
	final var duration = unit.between(start, end);
	
	final var offRange = Range.between(0L, 500L);
	final var traceRange = Range.between(501L, 1000L);
	final var debugRange = Range.between(1001L, 2000L);
	final var infoRange = Range.between(2001L, 3000L);
	final var warnRange = Range.between(3001L, 10000L);
	final var errorRange = Range.between(10001L, MAX_VALUE);
	
	final var ranges = List.of(
		new SimpleEntry<>(offRange, OFF),
		new SimpleEntry<>(traceRange, TRACE),
		new SimpleEntry<>(debugRange, DEBUG),
		new SimpleEntry<>(infoRange, INFO),
		new SimpleEntry<>(warnRange, WARN),
		new SimpleEntry<>(errorRange, ERROR)
	);
	
	for (final var rangeEntry: ranges) {
	    
	    final var range = rangeEntry.getKey();
	    
	    if (range.contains(duration)) {
		return rangeEntry.getValue();
	    }
	}
	
	return FATAL;
    }
}
