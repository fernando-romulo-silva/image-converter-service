package org.imageconverter.infra.exception;

/**
 * No data on CSV file.
 * 
 * @author Fernando Romulo da Silva
 */
public class CsvFileNoDataException extends CsvFileGenerationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CsvFileGenerationException exception with the specified detail message.
     * 
     * @param msg    The detail message
     * @param params The parameters used on message
     */
    public CsvFileNoDataException(final String msg, final Object... params) {
	super(msg, params);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg    The detail message
     * @param ex     The cause
     * @param params The parameters used on message
     */
    public CsvFileNoDataException(final String msg, final Throwable ex, final Object... params) {
	super(msg, ex, params);
    }
}
