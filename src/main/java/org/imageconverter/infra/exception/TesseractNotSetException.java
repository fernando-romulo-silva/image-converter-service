package org.imageconverter.infra.exception;

import static java.text.MessageFormat.format;
import static org.imageconverter.util.BeanUtil.getPropertyValue;

/**
 * Tesseract not properly configurated.
 * 
 * @author Fernando Romulo da Silva
 */
public class TesseractNotSetException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    private static final String TESSARCT_NOT_SET_MSG = "Tessarct configuration is invalid: folder {0}, language: {1} and dpi {2}.";

    /**
     * Constructs a new TesseractNotSetException exception with the specified detail message.
     * 
     */
    public TesseractNotSetException() {
	super(format(TESSARCT_NOT_SET_MSG, //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"))

	);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     * 
     * @param msg The detail message
     * @param ex  The cause
     */
    public TesseractNotSetException(final Throwable cause) {
	super(format(TESSARCT_NOT_SET_MSG, //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"), cause)

	);
    }
}
