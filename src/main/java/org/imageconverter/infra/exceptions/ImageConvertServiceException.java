package org.imageconverter.infra.exceptions;

public class ImageConvertServiceException extends ImageConverterServiceException {

    private static final long serialVersionUID = 1L;

    public ImageConvertServiceException(final String message) {
	super(message);
    }

    public ImageConvertServiceException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
