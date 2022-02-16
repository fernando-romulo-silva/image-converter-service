package org.imageconverter.util.logging;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Aspect to use beans validations on application.
 * 
 * @author Fernando Romulo da Silva
 */
@Aspect
@Component
public class ValidatorAspect {

    private final ValidatorFactory validatorFactory;

    /**
     * Default constructor.
     */
    public ValidatorAspect() {
	validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    /**
     * Execute the around with valition contraints.
     * 
     * @param point The object that support around advice in @AJ aspects
     * @return The metho's return
     * @throws Throwable If something wrong happens
     */
    @Around("execution(@javax.validation.constraints.* public * * (..))")
    public Object afterReturning(final ProceedingJoinPoint point) throws Throwable {

	final var theReturnValue = point.proceed();

	final var theSignature = (MethodSignature) point.getSignature();
	final var theValidator = validatorFactory.getValidator().forExecutables();
	final var theViolations = theValidator.validateReturnValue(point.getTarget(), theSignature.getMethod(), theReturnValue);

	if (theViolations.size() > 0) {
	    throw new ConstraintViolationException(theViolations);
	}

	return theReturnValue;
    }

    /**
     * Execute the around with valition contraints.
     * 
     * @param point The object that support around advice in @AJ aspects
     * @return The metho's return
     * @throws Throwable If something wrong happens
     */
    @Around("execution(public * * (.., @javax.validation.constraints..* (*), ..))")
    public Object pointcutMethodArgument(final ProceedingJoinPoint point) throws Throwable {

	final var theSignature = (MethodSignature) point.getSignature();
	final var theValidator = validatorFactory.getValidator().forExecutables();
	final var theViolations = theValidator.validateParameters(point.getTarget(), theSignature.getMethod(), point.getArgs());

	if (theViolations.size() > 0) {
	    throw new ConstraintViolationException(theViolations);
	}

	return point.proceed();
    }

}
