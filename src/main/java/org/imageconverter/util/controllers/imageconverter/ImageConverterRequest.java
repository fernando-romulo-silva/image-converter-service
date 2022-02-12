package org.imageconverter.util.controllers.imageconverter;

import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure to execute the image convertion.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageConverterRequest", description = "Resquest structure to convert Image")
public record ImageConverterRequest( //
		//
		@Schema(name = "file", description = "The uploaded image file", required = true, example = "image.bmp") //
		@NotNull(message = "The 'file' cannot be null") //
		MultipartFile file, //
		//
		@Schema(name = "executionType", description = "Execution's type", required = true, implementation = ExecutionType.class) //
		@NotNull(message = "The 'executionType' cannot be null") //
		ExecutionType executionType) implements ImageConverterRequestInterface {
}
