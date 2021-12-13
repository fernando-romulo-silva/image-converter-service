package org.imageconverter.domain.convertion;

import static org.apache.commons.lang3.StringUtils.upperCase;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ExecutionType {

    BATCH,

    WS,

    MS,
    
    WEB,
    
    DESKTOP;
    
    public static ExecutionType from(final String string) {
	   
	final var stringTypeNew = upperCase(string);
	
	return switch (stringTypeNew) { //
	        case "BATCH" -> BATCH; 
	        case "WS" -> WS; // Web Service
	        case "MS" -> MS; // Messaging Service
	        case "WEB" -> WEB; // Spring MVC
	        case "DESKTOP" -> DESKTOP; // 
	        default -> throw new IllegalArgumentException("Unknown execution type '%s'".formatted(string));
	};
    }
}
