package org.imageconverter.util.logging;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidatorAspect {

    private ValidatorFactory validatorFactory;

    public ValidatorAspect() {
	validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @Around("execution(@javax.validation.constraints.* public * * (..))")
    public Object afterReturning(final ProceedingJoinPoint aJoinPoint) throws Throwable {

	final var theReturnValue = aJoinPoint.proceed();

	final var theSignature = (MethodSignature) aJoinPoint.getSignature();
	final var theValidator = validatorFactory.getValidator().forExecutables();
	final var theViolations = theValidator.validateReturnValue(aJoinPoint.getTarget(), theSignature.getMethod(), theReturnValue);

	if (theViolations.size() > 0) {
	    throw new ConstraintViolationException(theViolations);
	}

	return theReturnValue;
    }
    
    @Around("execution(public * * (.., @javax.validation.constraints..* (*), ..))")
    public Object pointcutMethodArgument(final ProceedingJoinPoint aJoinPoint) throws Throwable {
	return validateInvocation(aJoinPoint);
    }

    private Object validateInvocation(final ProceedingJoinPoint aJoinPoint) throws Throwable {

	final var theSignature = (MethodSignature) aJoinPoint.getSignature();

	final var theValidator = validatorFactory.getValidator().forExecutables();

	final var theViolations = theValidator.validateParameters(aJoinPoint.getTarget(), theSignature.getMethod(), aJoinPoint.getArgs());

	if (theViolations.size() > 0) {
	    throw new ConstraintViolationException(theViolations);
	}

	return aJoinPoint.proceed();
    }

}
