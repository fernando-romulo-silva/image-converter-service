package org.imageconverter.domain;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@DisplayName("Test the image type entity")
@ExtendWith(MockitoExtension.class)
public class ImageTypeTest {

    private Validator validator;
    
    private ExecutableValidator executableValidator;
    
    public void setUp() {
	validator = buildDefaultValidatorFactory().getValidator();
	
	executableValidator = buildDefaultValidatorFactory().getValidator().forExecutables();
    }
    
}
