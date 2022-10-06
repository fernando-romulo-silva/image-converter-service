package org.imageconverter.util.controllers.imageconverter;

import org.imageconverter.domain.conversion.ExecutionType;

/**
 * Interface to execute the image conversion.
 * 
 * @author Fernando Romulo da Silva
 */
public sealed interface ImageConverterRequestInterface permits ImageConverterRequest, ImageConverterRequestArea {

    /**
     * File parameter.
     * 
     * @return a {@link String} object
     */
    String fileName();

    /**
     * File parameter.
     * 
     * @return a byte[] with file.
     */
    byte[] fileContent();

    /**
     * Execution type.
     * 
     * @return a {@link ExecutionType} object
     */
    ExecutionType executionType();
}
