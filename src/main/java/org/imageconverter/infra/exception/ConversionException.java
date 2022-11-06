package org.imageconverter.infra.exception;

/**
 * Conversion image file to text error.
 * 
 * @author Fernando Romulo da Silva
 */
public class ConversionException extends ElementInvalidException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConversionException exception with the specified detail message.
     * 
     * @param msg    The detail message
     * @param params The parameters used on message
     */
    public ConversionException(final String message, final Object... params) {
	super(message, params);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg    The detail message
     * @param ex     The cause
     * @param params The parameters used on message
     */
    public ConversionException(final String message, final Throwable cause, final Object... params) {
	super(message, cause, params);
    }
}
