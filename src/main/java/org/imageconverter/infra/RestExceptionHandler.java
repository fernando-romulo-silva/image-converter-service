package org.imageconverter.infra;

import static java.util.stream.Collectors.joining;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.imageconverter.infra.exception.ElementConflictException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.exception.ImageConvertServiceException;
import org.imageconverter.infra.exception.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * This class handle exceptions and create response properly.
 * 
 * @author Fernando Romulo da Silva
 */
@ControllerAdvice
public class RestExceptionHandler extends AbstractRestExceptionHandler {

    @ExceptionHandler(ElementNotFoundException.class)
    ResponseEntity<Object> handleElementNotFoundException(final ElementNotFoundException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ElementConflictException.class)
    ResponseEntity<Object> handleElementElementConflictException(final ElementConflictException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ElementInvalidException.class)
    ResponseEntity<Object> handleElementInvalidException(final ElementInvalidException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    ResponseEntity<Object> handleServiceUnavailableException(final ServiceUnavailableException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ImageConvertServiceException.class)
    ResponseEntity<Object> handleImageConvertException(final ImageConvertServiceException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex, final WebRequest request) {

	final var msg = ex.getConstraintViolations() //
			.stream() //
			.map(ConstraintViolation::getMessage) //
			.collect(joining(", "));

	return handleObjectException(msg, ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class) // HttpMessageNotReadableException
    ResponseEntity<Object> handleUnknownException(final Throwable ex, final WebRequest request) {

	final var msg = "Unexpected error. Please, check the log with traceId and spanId for more detail";

	return handleObjectException(msg, ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
