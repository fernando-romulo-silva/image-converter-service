package org.imageconverter.infra.util.controllers.imageconverter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.conversion.ExecutionType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure to execute the image conversion.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageConverterRequest", description = "Resquest structure to convert Image")
public record ImageConverterRequest( //
		
		@Schema(name = "fileName", description = "The uploaded image file name", required = true, example = "image.bmp") //
		@NotEmpty(message = "{imageConversion.fileName}") //
		String fileName, //
		//
		@Schema(name = "fileContent", description = "The uploaded image file bytes", required = true, example = "image.bmp") //
		@NotNull(message = "{imageConversion.fileContent}") //
		byte[] fileContent, //
		//
		@Schema(name = "executionType", description = "Execution's type", required = true, implementation = ExecutionType.class) //
		@NotNull(message = "{imageConversion.executionType}") //
		ExecutionType executionType) implements ImageConverterRequestInterface {
}
