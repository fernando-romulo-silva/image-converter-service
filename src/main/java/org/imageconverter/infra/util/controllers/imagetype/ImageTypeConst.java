package org.imageconverter.infra.util.controllers.imagetype;

/**
 * Image Type URLs.
 * 
 * @author Fernando Romulo da Silva
 */
public final class ImageTypeConst {

    public static final String REST_URL = "/rest/images/type";

    public static final String ACTION_URL = "/gui/images/type";

    private ImageTypeConst() {
	throw new IllegalStateException("You can't instanciate this class");
    }
}