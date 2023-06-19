package org.imageconverter.util.performance;

import static java.lang.Long.MAX_VALUE;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.springframework.boot.logging.LogLevel.DEBUG;
import static org.springframework.boot.logging.LogLevel.ERROR;
import static org.springframework.boot.logging.LogLevel.FATAL;
import static org.springframework.boot.logging.LogLevel.INFO;
import static org.springframework.boot.logging.LogLevel.OFF;
import static org.springframework.boot.logging.LogLevel.TRACE;
import static org.springframework.boot.logging.LogLevel.WARN;

import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMonitor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceMonitor.class);

    @Value("${spring.application.name:undefined}")
    private String applicationName;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

	final var start = Instant.now();

	try {

	    return invocation.proceed();

	} finally {

	    final var end = Instant.now();

	    final var duration = MILLIS.between(start, end);

	    final var logLevel = getLevel(duration);

	    if (isLogging(logLevel)) {

		final var message = "Performance duration [{}] milliseconds, project [{}], threadId [{}], class [{}], method [{}] with parameters [{}], returnType [{}], caller [{}], stackTrace {}";

		final var threadId = Thread.currentThread().getId();

		final var method = invocation.getMethod();

		final var clazz = invocation.getThis().getClass();
		final var declaredClazz = method.getDeclaringClass();

		final var methodName = method.getName();

		final var methodArgs = Stream.of(invocation.getArguments()) //
				.map(p -> isNull(p) ? "null" : p.toString()) //
				.toList();

		final var methodArgsType = Stream.of(method.getParameters()) //
				.map(Parameter::getType) //
				.map(Class::getSimpleName) //
				.toList();

		final var parameters = range(0, methodArgs.size()) //
				.boxed() //
				.map(i -> methodArgsType.get(i) + ":" + methodArgs.get(i)).collect(joining(","));

		final var methodReturnType = method.getReturnType();
		
		final var callerAndStack = findCallerAndStack();

		final var clazzString = Objects.equals(clazz, declaredClazz) // 
				? clazz.getName() // 
				: declaredClazz.getSimpleName() + ":" + clazz.getName();

		final Object[] params = { duration, applicationName, threadId, clazzString, methodName, parameters, methodReturnType, callerAndStack.getKey(), LF.concat(callerAndStack.getValue()) };

		executeLog(logLevel, message, params);
	    }
	}
    }

    private AbstractMap.SimpleEntry<String, String> findCallerAndStack() {

	final var stackList = StackWalker.getInstance() //
			.walk(this::walkThroughStack) //
			.stream() //
			.filter(this::filterNonMatter) //
			.map(trace -> substringBefore(trace.getClassName(), "$$EnhancerBySpringCGLIB") + "." + trace.getMethodName() + ":" + trace.getLineNumber()) //
			.toList();

	final var stackListNoTrap = stackList.stream() //
		   .filter(s -> !StringUtils.containsAnyIgnoreCase(s, ".filter.")) //
		   .toList();
	
	final var caller = stackListNoTrap.isEmpty() //
			? this.getClass().getSimpleName() // 
			: stackListNoTrap.get(stackListNoTrap.size() - 1);
	
	final var stackTrace = stackList.stream().collect(joining(LF));

	return new AbstractMap.SimpleEntry<>(caller, stackTrace);
    }

    private boolean filterNonMatter(final StackFrame stackFrame) {
	return !StringUtils.containsAnyIgnoreCase(stackFrame.getClassName(), this.getClass().getSimpleName()) //
			&& !StringUtils.containsAnyIgnoreCase(stackFrame.getClassName(), "$$FastClassBySpringCGLIB");
    }

    private List<StackFrame> walkThroughStack(final Stream<StackFrame> stackFrameStream) {
	return stackFrameStream //
			.filter(frame -> frame.getClassName().contains("org.imageconverter")) //
			.toList();
    }

    private void executeLog(final LogLevel level, final String message, final Object... arguments) {

	final var finalMsg = StringEscapeUtils.escapeJava(message);

	switch (level) {
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

	final var ranges = List.of( //
			new SimpleEntry<>(offRange, OFF), //
			new SimpleEntry<>(traceRange, TRACE), //
			new SimpleEntry<>(debugRange, DEBUG), //
			new SimpleEntry<>(infoRange, INFO), //
			new SimpleEntry<>(warnRange, WARN), //
			new SimpleEntry<>(errorRange, ERROR) //
	);

	return ranges.stream() //
			.filter(entry -> entry.getKey().contains(duration)) //
			.map(SimpleEntry::getValue) //
			.findFirst() //
			.orElse(FATAL);
    }

    public String createExpression(final Class<?> clazz, final Class<?>... parentClazzes) {

	final var pointCut = new StringBuilder();

	pointCut.append("execution ( public * ").append(clazz.getName()).append(".*(..))");

	for (final var parentClazz : parentClazzes) {
	    pointCut.append(" || (").append("execution ( public * ").append(parentClazz.getName()).append(".*(..))").append(" && target (").append(clazz.getName()).append(")").append(")");
	}

	return pointCut.toString();
    }

    private boolean isLogging(final LogLevel logLevel) {

	if (logLevel == OFF) {
	    return false;
	}

	if (logLevel == FATAL) {
	    return true;
	}

	return (logLevel == TRACE && LOGGER.isTraceEnabled()) //
			|| (logLevel == DEBUG && LOGGER.isDebugEnabled()) //
			|| (logLevel == INFO && LOGGER.isInfoEnabled()) //
			|| (logLevel == WARN && LOGGER.isWarnEnabled()) //
			|| (logLevel == ERROR && LOGGER.isErrorEnabled());
    }
}
