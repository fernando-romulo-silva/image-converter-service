package org.imageconverter.infra;

public class ConvertionImageException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    public ConvertionImageException(final String message) {
	super(message);
    }

    public ConvertionImageException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
