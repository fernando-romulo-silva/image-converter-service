package org.imageconverter.infra.exception;

/**
 * Tesseract convertion error.
 * 
 * @author Fernando Romulo da Silva
 */
public class TesseractConvertionException extends ServiceUnavailableException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TesseractConvertionException exception with the specified detail message.
     * 
     * @param msg The detail message
     */
    public TesseractConvertionException(final String message) {
	super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public TesseractConvertionException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
