package org.imageconverter.infra.exceptions;

public class ImageTypeNotFoundException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    public ImageTypeNotFoundException(final String extension) {
	super("Image type '" + extension + "' not found");
    }
}
