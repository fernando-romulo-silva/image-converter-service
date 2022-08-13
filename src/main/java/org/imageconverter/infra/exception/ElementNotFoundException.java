package org.imageconverter.infra.exception;

/**
 * Element not found on search.
 * 
 * @author Fernando Romulo da Silva
 */
public class ElementNotFoundException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ElementNotFoundException exception and create detail message regard of parameters. </br>
     * The exception detail msg will be: cls.getSimpleName() + " with id '" + id + "'  not found". </br>
     * For instance for Person object with id 10: </br>
     * "Person with id '10'  not found"
     * 
     * @param <T> The class type
     * @param cls Class element
     * @param id  Object id that repeated
     */
    public <T> ElementNotFoundException(final Class<T> cls, final Long id) {
	super(cls.getSimpleName() + " with id '" + id + "' not found");
    }

    /**
     * Constructs a new ElementNotFoundException exception and create detail message regard of parameters. </br>
     * The exception detail msg will be: cls.getSimpleName() + " with " + msg + " already exists". </br>
     * For instance for Person object and msg equals to "id '10' and name 'Fernando'": </br>
     * "Person with id '10' and name 'Fernando' not found"
     * 
     * @param <T> The class type
     * @param cls Class element
     * @param msg The specific message
     */
    public <T> ElementNotFoundException(final Class<T> cls, final String msg) {
	super(cls.getSimpleName() + " with " + msg + " not found");
    }
}
