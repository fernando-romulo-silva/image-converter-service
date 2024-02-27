package org.imageconverter.domain.conversion;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.upperCase;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Conversion execution's type.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(enumAsRef = true)
public enum ExecutionType {

    BATCH,

    REST,
    
    SOAP,

    MESSAGING,
    
    PAGE,
    
    DESKTOP;
    
    @Override
    public String toString() {

	return switch (this) { //
            case BATCH -> "B"; // BATCH CLIENT
            case SOAP -> "S"; // Web Service SOAP
            case REST -> "R"; // Web Service REST
            case MESSAGING -> "M"; // Messaging Service
            case PAGE -> "P"; // Spring MVC
            case DESKTOP -> "D"; // Swing? JavaFx? 	
            default -> null;
       };
    }
    
    /**
     * Convert string to objects
     * 
     * @param string The string to 
     * @return A object {@link ExecutionType} enum
     */
    public static ExecutionType from(final String string) {
	   
	final var stringTypeNew = ofNullable(upperCase(string)).orElse(EMPTY);
	
	return switch (stringTypeNew) { //
	        case "B" -> BATCH; // BATCH CLIENT
	        case "S" -> SOAP; // Web Service SOAP
	        case "R" -> REST; // Web Service REST
	        case "M" -> MESSAGING; // Messaging Service
	        case "P" -> PAGE; // Spring MVC
	        case "D" -> DESKTOP; // Swing? JavaFx? 
	        default -> null; // UNKNOWN ??
	};
    }
}
