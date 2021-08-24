package org.imageconverter.infra.exceptions;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public <T> NotFoundException(final Class<T> cls, final Long id) {
	super(cls.getSimpleName() + " with id '" + id + "' not found");
    }

    public <T> NotFoundException(final Class<T> cls, final String msg) {
	super(cls.getSimpleName() + " with " + msg + " not found");
    }

//    public Long getObjIdentifier() {
//	return objIdentifier;
//    }
}
