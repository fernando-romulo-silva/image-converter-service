package org.imageconverter.infra.exceptions;

import static java.text.MessageFormat.format;
import static org.imageconverter.util.BeanUtil.getPropertyValue;

public class TesseractNotSetException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    public TesseractNotSetException() {
	super(format("Tessarct configuration is invalid: folder {0}, language: {1} and dpi {2}.", //
			getPropertyValue("tesseract.folder"), //
			getPropertyValue("tesseract.language"), //
			getPropertyValue("tesseract.dpi"))

	);
    }

}
