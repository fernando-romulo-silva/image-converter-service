package org.imageconverter.infra.exception;

/**
 * Service not available for convertion.
 * 
 * @author Fernando Romulo da Silva
 */
public class ServiceUnavailableException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ServiceUnavailableException exception with the specified detail message.
     * 
     * @param msg The detail message
     */
    public ServiceUnavailableException(final String message) {
	super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public ServiceUnavailableException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
