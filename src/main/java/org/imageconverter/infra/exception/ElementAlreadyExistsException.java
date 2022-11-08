package org.imageconverter.infra.exception;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Element already exists exception.
 * 
 * @author Fernando Romulo da Silva
 */
public class ElementAlreadyExistsException extends ElementConflictException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ElementAlreadyExistsException exception and create detail message regard of parameters. </br>
     * For instance for Person object with id 10: </br>
     * "Person with id '10' already exists"
     * 
     * @param <T> The class type
     * @param cls Class element
     * @param id  Object id that repeated
     */
    public <T> ElementAlreadyExistsException(final Class<T> cls, final Object id) {
	super("{exception.elementIdAlreadyExists}", cls.getSimpleName(), id); //
    }

    /**
     * Constructs a new ElementAlreadyExistsException exception and create detail message regard of parameters. </br>
     * The exception detail msg will be: cls.getSimpleName() + " with " + msg + " already exists". </br>
     * For instance for Person object and msg equals to "id '10' and name 'Fernando'": </br>
     * "Person with id '10' and name 'Fernando' already exists"
     * 
     * @param <T> The class type
     * @param cls Class element
     * @param msg The specific message
     */
    public <T> ElementAlreadyExistsException(final Class<T> cls, final Object[] params) {
	super("{exception.elementFieldsAlreadyExists}", ArrayUtils.addAll(new Object[] { cls.getSimpleName() }, params));
    }
}
