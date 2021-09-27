package org.imageconverter.infra.exceptions;

public class ElementAlreadyExistsException extends ElementConflictException {

    private static final long serialVersionUID = 1L;

    public <T> ElementAlreadyExistsException(final Class<T> cls, final Long id) {
	super(cls.getSimpleName() + " with id '" + id + "' already exists");
    }

    public <T> ElementAlreadyExistsException(final Class<T> cls, final String msg) {
	super(cls.getSimpleName() + " with " + msg + " already exists");
    }
}
