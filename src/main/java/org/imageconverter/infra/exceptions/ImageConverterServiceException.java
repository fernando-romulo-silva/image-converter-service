package org.imageconverter.infra.exceptions;

public class ImageConverterServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImageConverterServiceException(final String msg) {
	super(msg);
    }

    public ImageConverterServiceException(final String msg, final Throwable ex) {
	super(msg, ex);
    }
}
