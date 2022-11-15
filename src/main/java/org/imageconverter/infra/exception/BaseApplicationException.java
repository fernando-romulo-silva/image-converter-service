package org.imageconverter.infra.exception;

import static org.imageconverter.util.BeanUtil.getBeanFrom;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Base applicaton exception.
 * 
 * @author Fernando Romulo da Silva
 */
public class BaseApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BaseApplicationException exception with the specified detail message.
     * 
     * @param msg    The detail message
     * @param params The parameters used on message
     */
    public BaseApplicationException(final String msg, final Object... params) {
	super(getFinalMessage(msg, params));
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg    The detail message
     * @param ex     The cause
     * @param params The parameters used on message
     */
    public BaseApplicationException(final String msg, final Throwable ex, final Object... params) {
	super(getFinalMessage(msg, params), ex);
    }

    private static String getFinalMessage(final String msg, final Object... params) {

	if (StringUtils.containsNone(msg, '{', '}')) {
	    return msg;
	}
	
	final var code = RegExUtils.replaceAll(msg, "[{}]", "");
	final var messageSource = getBeanFrom(MessageSource.class);
	final var locale = LocaleContextHolder.getLocale();

	return messageSource.getMessage(code, params, locale);
    }
}
