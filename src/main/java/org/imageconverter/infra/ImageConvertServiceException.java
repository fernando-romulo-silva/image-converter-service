package org.imageconverter.infra;

public class ImageConvertServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public ImageConvertServiceException(final String message) {
	super(message);
    }

    public ImageConvertServiceException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
