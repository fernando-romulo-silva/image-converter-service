package org.imageconverter.infra.exception;

/**
 * Element with invalid state/parameters.
 * 
 * @author Fernando Romulo da Silva
 */
public class ElementInvalidException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConversionException exception with the specified detail message.
     * 
     * @param msg    The detail message
     * @param params The parameters used on message
     */
    public ElementInvalidException(final String msg, final Object... params) {
	super(msg, params);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg    The detail message
     * @param ex     The cause
     * @param params The parameters used on message
     */
    public ElementInvalidException(final String msg, final Throwable ex, final Object... params) {
	super(msg, ex, params);
    }

//    public <T> ElementInvalidException(final Class<T> clazz, final String msg) {
//	super(clazz.getSimpleName() + " is invalid: " + msg);
//    }
}
