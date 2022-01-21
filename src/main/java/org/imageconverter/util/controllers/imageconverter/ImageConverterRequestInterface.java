package org.imageconverter.util.controllers.imageconverter;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public sealed interface ImageConverterRequestInterface permits ImageConverterRequest, ImageConverterRequestArea {

    MultipartFile file();
    
    ExecutionType executionType();
}
