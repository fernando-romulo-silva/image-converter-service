package org.imageconverter.infra.exceptions;

public class ElementInvalidException extends ImageConverterServiceException {

    private static final long serialVersionUID = 1L;

    public ElementInvalidException(final String msg) {
	super(msg);
    }

    public ElementInvalidException(final String msg, final Throwable ex) {
	super(msg, ex);
    }

    public <T> ElementInvalidException(final Class<T> clazz) {
	super(clazz.getSimpleName() + " is invalid ");
    }

    public <T> ElementInvalidException(final Class<T> clazz, final String msg) {
	super(clazz.getSimpleName() + " is invalid: " + msg);
    }
}
