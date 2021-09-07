package org.imageconverter.infra;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ElementAlreadyExistsException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.infra.exceptions.ImageConvertServiceException;
import org.imageconverter.infra.exceptions.TesseractConvertionException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Object> handleElementTypeNotFoundException(final ElementNotFoundException ex, final WebRequest request) {

	return handleObjectException(ex, request, NOT_FOUND);
    }

    @ExceptionHandler(ElementAlreadyExistsException.class)
    public ResponseEntity<Object> handleIElementAlreadyExistsException(final ElementAlreadyExistsException ex, final WebRequest request) {

	return handleObjectException(ex, request, CONFLICT);
    }

    @ExceptionHandler(ConvertionException.class)
    public ResponseEntity<Object> handleConvertionImageException(final ConvertionException ex, final WebRequest request) {

	return handleObjectException(ex, request, BAD_REQUEST);
    }

    @ExceptionHandler(TesseractConvertionException.class)
    public ResponseEntity<Object> handleTesseractException(final TesseractConvertionException ex, final WebRequest request) {

	final var msg = "Error when execute tesseract";

	return handleObjectException(msg, ex, request, SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ImageConvertServiceException.class)
    public ResponseEntity<Object> handleImageConvertException(final ImageConvertServiceException ex, final WebRequest request) {

	return handleObjectException(ex, request, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

	final var errors = new HashMap<String, String>();

	ex.getBindingResult().getFieldErrors().forEach((error) -> {
	    final var fieldName = error.getField();
	    final var errorMessage = error.getDefaultMessage();
	    errors.put(fieldName, errorMessage);
	});

	final var msg = errors.values().stream().collect(joining(","));

	return handleObjectException(msg, ex, request, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

	final var msg = "Resource not found. Please check the /swagger-ui/ for more information";

	return handleObjectException(msg, ex, request, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleUnknownException(final Throwable ex, final WebRequest request) {

	final var msg = "Unexpected error. Please, check the log with traceId and spanId for more detail";

	return handleObjectException(msg, ex, request, INTERNAL_SERVER_ERROR);
    }

    // ==========================================================================================================================================

    private ResponseEntity<Object> handleObjectException(final Throwable ex, final WebRequest request, final HttpStatus status) {

	final var body = buildResponseBody(getRootCauseMessage(ex), status, ex, request);

	logger.error(getRootCauseMessage(ex), getRootCause(ex));

	return new ResponseEntity<>(body, status);
    }

    private ResponseEntity<Object> handleObjectException(final String msg, final Throwable ex, final WebRequest request, final HttpStatus status) {

	final var body = buildResponseBody(msg, status, ex, request);

	logger.error(getRootCauseMessage(ex), getRootCause(ex));

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

    // ?trace=true
    private boolean isTraceOn(final WebRequest request) {

	final var value = request.getParameterValues("trace");

	return Objects.nonNull(value) //
			&& value.length > 0 //
			&& value[0].contentEquals("true");
    }
}
