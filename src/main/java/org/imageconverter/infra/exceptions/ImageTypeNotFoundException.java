package org.imageconverter.infra.exceptions;

public class ImageTypeNotFoundException extends ImageConvertServiceException {

    private static final long serialVersionUID = 1L;

    public ImageTypeNotFoundException(final String extension) {
	super("Image type with extension '" + extension + "' not found");
    }

    public ImageTypeNotFoundException(final Long id) {
	super("Image type with id '" + id + "' not found");
    }
}
