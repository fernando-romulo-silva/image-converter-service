package org.imageconverter.infra.exceptions;

public class ElementConflictException extends ImageConverterServiceException {

    private static final long serialVersionUID = 1L;

    public ElementConflictException(final String msg) {
	super(msg);
    }

    public <T> ElementConflictException(final String msg, final Throwable ex) {
	super(msg, ex);
    }
}
