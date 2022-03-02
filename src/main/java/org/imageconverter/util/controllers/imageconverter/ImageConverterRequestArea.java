package org.imageconverter.util.controllers.imageconverter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ExecutionType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure with area (x, y, width and height) to execute the image convertion.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageConverterRequestArea", description = "Resquest structure to convert Image with specific area")
public record ImageConverterRequestArea( //
		
		@Schema(name = "fileName", description = "The uploaded image file name", required = true, example = "image.bmp") //
		@NotEmpty(message = "The 'fileName' cannot be empty") //
		String fileName, //
		//
		@Schema(name = "fileContent", description = "The uploaded image file bytes", required = true, example = "image.bmp") //
		@NotNull(message = "The 'fileContent' cannot be null") //
		byte[] fileContent, //
		//
		@Schema(name = "executionType", description = "Execution's type", required = true, implementation = ExecutionType.class) //
		@NotNull(message = "The executionType cannot be null") //
		ExecutionType executionType, //
		//
		@Schema(name = "xAxis", description = "The x axis image point", required = false, example = "145") //
		@Min(value = 0, message = "The x point must be greater than zero") //
		Integer xAxis, //
		//
		@Schema(name = "yAxis", description = "The y axis image point", required = false, example = "123") //
		@Min(value = 0, message = "The y point must be greater than zero") //
		Integer yAxis, //
		//
		@Schema(name = "width", description = "The width area", required = false, example = "123") //
		@Min(value = 0, message = "The with must be greater than zero") //
		Integer width, //
		//
		@Schema(name = "height", description = "The height area", required = false, example = "343") //
		@Min(value = 0, message = "The height must be greater than zero") //
		Integer height) implements ImageConverterRequestInterface {
    
}