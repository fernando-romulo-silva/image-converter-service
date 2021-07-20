package org.imageconverter.infra;

public class TesseractConvertionException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    public TesseractConvertionException(final String message) {
	super(message);
    }

    public TesseractConvertionException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
