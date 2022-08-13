package org.imageconverter.infra.exception;

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
     * @param msg The detail message
     */
    public BaseApplicationException(final String msg) {
	super(msg);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public BaseApplicationException(final String msg, final Throwable ex) {
	super(msg, ex);
    }
}
