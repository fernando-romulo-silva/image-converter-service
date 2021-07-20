package org.imageconverter.infra;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ConvertionImageException.class)
    public ResponseEntity<Object> handleConvertionImageException(final ConvertionImageException ex, final WebRequest request) {
	
	final var status = HttpStatus.BAD_REQUEST;
	
	final var body = buildResponseBody(getRootCauseMessage(ex), status, ex, request);

	logger.error(getRootCauseMessage(ex), getRootCause(ex));

	return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(TesseractConvertionException.class)
    public ResponseEntity<Object> handleTesseractException(final TesseractConvertionException ex, final WebRequest request) {
	
	final var status = HttpStatus.SERVICE_UNAVAILABLE;
	
	final var body = buildResponseBody(getRootCauseMessage(ex), status, ex, request);

	logger.error(getRootCauseMessage(ex), getRootCause(ex));

	return new ResponseEntity<>(body, status);
    }
    
    @ExceptionHandler(ImageConvertServiceException.class)
    public ResponseEntity<Object> handleImageConvertException(final ImageConvertServiceException ex, final WebRequest request) {
	
	final var status = HttpStatus.INTERNAL_SERVER_ERROR;
	
	final var body = buildResponseBody(getRootCauseMessage(ex), status, ex, request);

	logger.error(getRootCauseMessage(ex), getRootCause(ex));

	return new ResponseEntity<>(body, status);
    }
    

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleUnknownException(final Exception ex, final WebRequest request) {

	final var status = HttpStatus.INTERNAL_SERVER_ERROR;
	
	final var msg = "Unexpected error. Please, check the log with traceId and spanId for more detail";

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
