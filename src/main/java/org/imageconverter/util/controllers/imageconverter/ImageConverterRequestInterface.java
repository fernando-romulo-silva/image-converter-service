package org.imageconverter.util.controllers.imageconverter;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface to execute the image convertion.
 * 
 * @author Fernando Romulo da Silva
 */
public sealed interface ImageConverterRequestInterface permits ImageConverterRequest,ImageConverterRequestArea {

    /**
     * File parameter.
     * 
     * @return a {@link MultipartFile} object
     */
    MultipartFile file();

    /**
     * Execution type.
     * 
     * @return a {@link ExecutionType} object
     */
    ExecutionType executionType();
}
