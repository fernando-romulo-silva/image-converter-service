package org.imageconverter.infra.exception;

/**
 * Tesseract conversion error.
 * 
 * @author Fernando Romulo da Silva
 */
public class TesseractConversionException extends ServiceUnavailableException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new execption with the specified detail message.
     * 
     * @param msg    The detail message
     * @param params The parameters used on message
     */
    public TesseractConversionException(final String message, final Object... params) {
	super(message, params);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param msg    The detail message
     * @param ex     The cause
     * @param params The parameters used on message
     */
    public TesseractConversionException(final String message, final Throwable cause, final Object... params) {
	super(message, cause, params);
    }
}
