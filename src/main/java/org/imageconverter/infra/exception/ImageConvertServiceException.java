package org.imageconverter.infra.exception;

/**
 * Convert image exception
 * 
 * @author Fernando Romulo da Silva
 */
public class ImageConvertServiceException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ImageConvertServiceException exception with the specified detail message.
     * 
     * @param msg The detail message
     */
    public ImageConvertServiceException(final String message) {
	super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public ImageConvertServiceException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
