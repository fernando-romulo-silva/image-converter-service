package org.imageconverter.domain;

import static org.apache.commons.lang3.StringUtils.upperCase;

public enum ExecutionStatus {

    DONE,
    
    ERROR;
    
    public static ExecutionStatus from(final String string) {
	   
	final var stringTypeNew = upperCase(string);
	
	return switch (stringTypeNew) { //
	        case "DONE" -> DONE;
	        case "ERROR" -> ERROR;
	        default -> throw new IllegalArgumentException("Unknown status '%s'".formatted(string));
	};
    }
}
