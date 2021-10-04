package org.imageconverter.util.controllers;

import org.imageconverter.domain.imageConvertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageConverterRequestInterface {

    MultipartFile data();
    
    ExecutionType executionType();
}
