package org.imageconverter.infra.exceptions;

public class ElementNotFoundException extends ImageConverterServiceException {

    private static final long serialVersionUID = 1L;

    public <T> ElementNotFoundException(final Class<T> cls, final Long id) {
	super(cls.getSimpleName() + " with id '" + id + "' not found");
    }

    public <T> ElementNotFoundException(final Class<T> cls, final String msg) {
	super(cls.getSimpleName() + " with " + msg + " not found");
    }
}
