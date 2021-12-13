package org.imageconverter.util.controllers.imageconverter;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageConverterRequestInterface {

    MultipartFile file();
    
    ExecutionType executionType();
}
