package org.imageconverter.infra.exceptions;

/**
 * Element not found on search.
 * 
 * @author Fernando Romulo da Silva
 */
public class ElementNotFoundException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    public <T> ElementNotFoundException(final Class<T> cls, final Long id) {
	super(cls.getSimpleName() + " with id '" + id + "' not found");
    }

    public <T> ElementNotFoundException(final Class<T> cls, final String msg) {
	super(cls.getSimpleName() + " with " + msg + " not found");
    }
}
