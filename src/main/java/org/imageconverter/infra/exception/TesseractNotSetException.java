package org.imageconverter.infra.exception;

import static org.imageconverter.util.BeanUtil.getPropertyValue;

/**
 * Tesseract not properly configurated.
 * 
 * @author Fernando Romulo da Silva
 */
public class TesseractNotSetException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TesseractNotSetException exception with the specified detail message.
     * 
     */
    public TesseractNotSetException() {
	super("{exception.tesseractNotSet}", //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi") //

	);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public TesseractNotSetException(final Throwable cause) {
	super("{exception.tesseractNotSet}", //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"), cause

	);
    }
}
