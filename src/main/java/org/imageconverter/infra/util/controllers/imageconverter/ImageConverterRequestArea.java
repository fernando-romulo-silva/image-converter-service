package org.imageconverter.infra.util.controllers.imageconverter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.conversion.ExecutionType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure with area (x, y, width and height) to execute the image conversion.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageConverterRequestArea", description = "Resquest structure to convert Image with specific area")
public record ImageConverterRequestArea( //
		
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
		ExecutionType executionType, //
		//
		@Schema(name = "xAxis", description = "The x axis image point", required = false, example = "145") //
		@Min(value = 0, message = "{imageConversion.xAxis}") //
		Integer xAxis, //
		//
		@Schema(name = "yAxis", description = "The y axis image point", required = false, example = "123") //
		@Min(value = 0, message = "{imageConversion.yAxis}") //
		Integer yAxis, //
		//
		@Schema(name = "width", description = "The width area", required = false, example = "123") //
		@Min(value = 0, message = "{imageConversion.width}") //
		Integer width, //
		//
		@Schema(name = "height", description = "The height area", required = false, example = "343") //
		@Min(value = 0, message = "{imageConversion.height}") //
		Integer height) implements ImageConverterRequestInterface {
    
}