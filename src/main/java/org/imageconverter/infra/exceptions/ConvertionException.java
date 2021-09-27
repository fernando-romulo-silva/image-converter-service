package org.imageconverter.infra.exceptions;

public class ConvertionException extends ElementInvalidException {

    private static final long serialVersionUID = 1L;

    public ConvertionException(final String message) {
	super(message);
    }

    public ConvertionException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
