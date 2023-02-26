package org.imageconverter.util.performance;

import static java.lang.Long.MAX_VALUE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.springframework.boot.logging.LogLevel.DEBUG;
import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.FATAL;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.boot.logging.LogLevel.OFF;
import static org.springframework.boot.logging.LogLevel.TRACE;
import static org.springframework.boot.logging.LogLevel.WARN;

import java.lang.reflect.Parameter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.Range;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMonitor implements MethodInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceMonitor.class);

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
	
	final var method = invocation.getMethod();
	
	final var clazz = method.getDeclaringClass();
	
	final var methodName = method.getName();
	
	final var methodArgs = Stream.of(invocation.getArguments())
				.map(Object::toString)
				.toList();
	
	final var methodArgsType = Stream.of(method.getParameters())
			.map(Parameter::getType) //
			.map(Class::getSimpleName) //
			.toList();
	
	final var parameters = range(0, methodArgs.size())
			.boxed()
			.collect(toMap(methodArgsType::get, methodArgs::get))
			.entrySet().stream()
			.map(entry -> entry.getKey() + ":" + entry.getValue())
			.collect(joining(","));
			
	final var start = Instant.now();
	try {
	  
	    return invocation.proceed();
	    
	} finally {

	    final var end = Instant.now();

	    final var unit = ChronoUnit.MILLIS;

	    final var duration = unit.between(start, end);

	    final var logLevel = getLevel(duration);

	    final var message = "Performance duration '{}' class '{}', method '{}' parameters '[{}]'";

	    final Object[] params = { duration, clazz, methodName, parameters };

	    executeLog(logLevel, message, params);
	}
    }
    
    private void executeLog(final LogLevel level, final String message, final Object... arguments) {
	if (OFF.equals(level)) {
	    return;
	}
	
	final var finalMsg = StringEscapeUtils.escapeJava(message);
	
	switch(level) {
		case TRACE -> LOGGER.trace(finalMsg, arguments);
		case DEBUG -> LOGGER.debug(finalMsg, arguments);
		case INFO -> LOGGER.info(finalMsg, arguments);
		case WARN -> LOGGER.warn(finalMsg, arguments);
		case ERROR, FATAL -> LOGGER.error(finalMsg, arguments);
		default -> LOGGER.debug(finalMsg, arguments);
	}
    }
    
    private LogLevel getLevel(final long duration) {
	
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
	
	return ranges.stream() //
			.filter(entry -> entry.getKey().contains(duration)) //
			.map(SimpleEntry::getValue) //
			.findFirst() //
			.orElse(FATAL);
    }
    
    public String createExpression(final Class<?> clazz, final Class<?> ...parentClazzes) {
	
	final var pointCut = new StringBuilder();
	
	pointCut.append("execution ( public * ").append(clazz.getName()).append(".*(..))");
	
	for (final var parentClazz : parentClazzes) {
	    pointCut.append(" || (")
	    	.append("execution ( public * ").append(parentClazz.getName()).append(".*(..))")
	    	.append(" && target (").append(clazz.getName()).append(")")
		.append(")");
	}
	
	return pointCut.toString();
    }
}
