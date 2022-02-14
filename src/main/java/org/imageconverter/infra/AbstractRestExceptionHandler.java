package org.imageconverter.infra;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.imageconverter.infra.exceptions.BaseApplicationException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Defaults handlers methods
 * 
 * @author Fernando Romulo da Silva
 */
abstract class AbstractRestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
		    final WebRequest request) {

	final var msg = "MissingServletRequestParameterException: The parameter '" + ex.getParameterName() + "' is missing";

	return handleObjectException(msg, ex, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

	return handleObjectException(ex, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

	final var errors = new HashMap<String, String>();

	ex.getBindingResult().getFieldErrors().forEach((error) -> {
	    final var fieldName = error.getField();
	    final var errorMessage = error.getDefaultMessage();
	    errors.put(fieldName, errorMessage);
	});

	final var msg = errors.values().stream().collect(joining(", "));

	return handleObjectException(msg, ex, request, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

	final var msg = "Resource not found. Please check the /swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config for more information";

	return handleObjectException(msg, ex, request, status);
    }

    /**
     * Handle exceptions, especially {@link BaseApplicationException} and its descendents and use the exception's message.
     * 
     * @param ex      The exception
     * @param request Request object, to create response body.
     * @param status  The error status
     * @return A {@link ResponseEntity} object that's the response
     */
    protected ResponseEntity<Object> handleObjectException(final Throwable ex, final WebRequest request, final HttpStatus status) {

	final var msg = ex instanceof BaseApplicationException ? escapeJava(getMessage(ex)) : escapeJava(getRootCauseMessage(ex));

	final var body = buildResponseBody(msg, status, ex, request);

	if (logger.isErrorEnabled()) {
	    logger.error(msg, getRootCause(ex));
	}

	return new ResponseEntity<>(body, status);
    }

    /**
     * Handle exceptions using the message on response.
     * 
     * @param msg     The message that it'll be used
     * @param ex      The exception
     * @param request Request object, to create response body.
     * @param status  The error status
     * @return A {@link ResponseEntity} object that's the response
     */
    protected ResponseEntity<Object> handleObjectException(final String msg, final Throwable ex, final WebRequest request, final HttpStatus status) {

	final var body = buildResponseBody(msg, status, ex, request);

	if (logger.isErrorEnabled()) {
	    logger.error(escapeJava(getRootCauseMessage(ex)), getRootCause(ex));
	}

	return new ResponseEntity<>(body, status);
    }

    private Map<String, Object> buildResponseBody(final String errorMessage, final HttpStatus status, final Throwable ex, final WebRequest request) {
	final var body = new LinkedHashMap<String, Object>();

	body.put("timestamp", LocalDateTime.now().format(ISO_DATE_TIME));
	body.put("status", status.value());
	body.put("error", status.getReasonPhrase());
	body.put("message", errorMessage);
	body.put("traceId", MDC.get("traceId"));
	body.put("spanId", MDC.get("spanId"));

	if (isTraceOn(request)) {
	    body.put("stackTrace", getStackTrace(ex));
	}

	return body;
    }

    private boolean isTraceOn(final WebRequest request) {

	final var value = request.getParameterValues("trace");

	return Objects.nonNull(value) //
			&& value.length > 0 //
			&& "true".contentEquals(value[0]);
    }

}