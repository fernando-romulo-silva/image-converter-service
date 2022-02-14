package org.imageconverter.infra.exceptions;

/**
 * Element conflits exception, like delete a linked object.
 * 
 * @author Fernando Romulo da Silva
 */
public class ElementConflictException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ElementConflictException exception with the specified detail message.
     * 
     * @param msg The detail message
     */
    public ElementConflictException(final String msg) {
	super(msg);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public ElementConflictException(final String msg, final Throwable ex) {
	super(msg, ex);
    }
}
