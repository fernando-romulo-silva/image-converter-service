package org.imageconverter.infra.exceptions;

import static java.text.MessageFormat.format;
import static org.imageconverter.util.BeanUtil.getPropertyValue;

public class TesseractNotSetException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;
    
    private static final String TESSARCT_NOT_SET_MSG = "Tessarct configuration is invalid: folder {0}, language: {1} and dpi {2}.";

    public TesseractNotSetException(final Throwable cause) {
	super(format(TESSARCT_NOT_SET_MSG, //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"), cause)

	);
    }
    
    public TesseractNotSetException() {
	super(format(TESSARCT_NOT_SET_MSG, //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"))

	);
    }    
}
