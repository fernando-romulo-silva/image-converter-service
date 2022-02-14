package org.imageconverter.infra.exceptions;

public class ServiceUnavailableException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException(final String message) {
	super(message);
    }

    public ServiceUnavailableException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
