package org.imageconverter.util.controllers.imageconverter;

import org.imageconverter.domain.imageConvertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageConverterRequestInterface {

    MultipartFile data();
    
    ExecutionType executionType();
}
