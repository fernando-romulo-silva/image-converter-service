package org.imageconverter.util.controllers.imageconverter;

/**
 * Image Converter URLs.
 * 
 * @author Fernando Romulo da Silva
 */
public final class ImageConverterConst {

    public static final String BASE_URL = "/image-converter";

    public static final String REST_URL = "/rest/images/convertion";

    public static final String ACTION_URL = "/gui/images/convertion";

    private ImageConverterConst() {
	throw new IllegalStateException("You can't instanciate this class");
    }
}