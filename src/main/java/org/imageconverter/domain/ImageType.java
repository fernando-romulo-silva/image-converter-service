package org.imageconverter.domain;

import static org.apache.commons.lang3.StringUtils.upperCase;

import org.imageconverter.infra.ConvertionImageException;

public enum ImageType {

    PNG,
    
    BMP,
    
    JPEG,
    
    JPG;
    
    public static ImageType from(final String string) {
	   
	final var stringTypeNew = upperCase(string);
	
	return switch (stringTypeNew) { //
	        case "PNG" -> PNG;
	        case "JPEG" -> JPEG;
	        case "BMP" -> BMP;
	        case "JPG" -> JPG;
	        default -> throw new ConvertionImageException("Unknown image type '%s'".formatted(string));
	};
    }
}
