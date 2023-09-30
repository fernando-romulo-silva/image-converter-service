package org.imageconverter.infra.util.controllers.imageconverter;

/**
 * Image Converter URLs.
 * 
 * @author Fernando Romulo da Silva
 */
public final class ImageConverterConst {

    public static final String REST_URL = "/rest/images/conversion";

    public static final String REST_URL_AREA = "/rest/images/conversion/area";
    
    public static final String ACTION_URL = "/gui/images/conversion";

    private ImageConverterConst() {
	throw new IllegalStateException("You can't instanciate this class");
    }
}