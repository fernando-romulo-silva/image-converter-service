package org.imageconverter.infra.exception;

import java.text.NumberFormat;

public class ElementWithIdNotFoundException extends ElementNotFoundException {

    private static final long serialVersionUID = 1L;

    public <T> ElementWithIdNotFoundException(final Class<T> cls, final Object id) {
	super("{exception.elementIdNotFound}", new Object[] { cls.getSimpleName(), formatNumber(id) });
    }

    public static Object formatNumber(final Object id) {

	if (id instanceof Number number) {

	    final var format = NumberFormat.getInstance();
	    format.setGroupingUsed(false);

	    return format.format(number);
	}

	return id;
    }
}
