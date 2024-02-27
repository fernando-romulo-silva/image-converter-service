package org.imageconverter.infra;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.RegExUtils;
import org.imageconverter.infra.exception.BaseApplicationException;
import org.imageconverter.infra.exception.CsvFileGenerationException;
import org.imageconverter.infra.exception.CsvFileNoDataException;
import org.imageconverter.infra.exception.ElementConflictException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.exception.ImageConvertServiceException;
import org.imageconverter.infra.exception.ServiceUnavailableException;
import org.springframework.context.MessageSource;
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

    /**
     * Default constructor.
     * 
     * @param messageSource Object used to translate messages
     */
    protected RestExceptionHandler(final MessageSource messageSource) {
	super(messageSource);
    }

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
    
    @ExceptionHandler(CsvFileGenerationException.class)
    ResponseEntity<Object> handleCsvFileGenerationException(final CsvFileGenerationException ex, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(CsvFileNoDataException.class)
    ResponseEntity<Object> handleCsvFileNoDataException(final CsvFileNoDataException ex, final WebRequest request) {
	return handleObjectException(ex, request, HttpStatus.NOT_FOUND);
    }
    
    
    @ExceptionHandler(BaseApplicationException.class)
    ResponseEntity<Object> handleBaseApplicationException(final BaseApplicationException ex, final WebRequest request) {
	return handleObjectException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex, final WebRequest request) {

	final var subErrors = new ArrayList<Map<String, String>>();

	final var locale = request.getLocale();

	for (final var violation : ex.getConstraintViolations()) {
	    final var errors = new HashMap<String, String>();

	    errors.put("field", substringAfterLast(violation.getPropertyPath().toString(), "."));
	    errors.put("value", violation.getInvalidValue() == null ? "null" : violation.getInvalidValue().toString());

	    final var code = RegExUtils.replaceAll(violation.getMessage(), "[{}]", "");
	    errors.put("error", messageSource.getMessage(code, null, locale)); // violation.getMessage());

	    subErrors.add(errors);
	}

	final var msg = messageSource.getMessage("exception.handleConstraintViolation", null, locale);

	return handleObjectException(msg, subErrors, ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class) // HttpMessageNotReadableException
    ResponseEntity<Object> handleUnknownException(final Throwable ex, final WebRequest request) {

	final var locale = request.getLocale();

	final var msg = messageSource.getMessage("exception.handleUnknown", null, locale);

	return handleObjectException(msg, List.of(), ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
