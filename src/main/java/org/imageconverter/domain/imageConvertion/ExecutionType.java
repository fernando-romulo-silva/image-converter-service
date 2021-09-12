package org.imageconverter.domain.imageConvertion;

import static org.apache.commons.lang3.StringUtils.upperCase;

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
	        case "WS" -> WS;
	        case "MS" -> MS;
	        case "WEB" -> WEB;
	        case "DESKTOP" -> DESKTOP;	        
	        default -> throw new IllegalArgumentException("Unknown execution type '%s'".formatted(string));
	};
    }
}
