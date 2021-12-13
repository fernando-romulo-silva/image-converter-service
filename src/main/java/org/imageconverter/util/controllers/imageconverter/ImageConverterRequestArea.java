package org.imageconverter.util.controllers.imageconverter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageConverterRequestArea", description = "Resquest structure to convert Image with specific area")
public record ImageConverterRequestArea( //

		@Schema(name = "file", description = "The uploaded image file", required = true, example = "image.bmp") //
		@NotNull(message = "The file cannot be null") //
		MultipartFile file, //
		//
		@Schema(name = "executionType", description = "Execution's type", required = true, implementation = ExecutionType.class) //
		@NotNull(message = "The executionType cannot be null") //
		ExecutionType executionType, //
		//
		@Schema(name = "x", description = "The x axis image point", required = false, example = "145") //
		@Min(value = 0, message = "The x point must be greater than zero") //
		Integer x, //
		//
		@Schema(name = "y", description = "The y axis image point", required = false, example = "123") //
		@Min(value = 0, message = "The y point must be greater than zero") //
		Integer y, //
		//
		@Schema(name = "width", description = "The width area", required = false, example = "123") //
		@Min(value = 0, message = "The with must be greater than zero") //
		Integer width, //
		//
		@Schema(name = "height", description = "The height area", required = false, example = "343") //
		@Min(value = 0, message = "The height must be greater than zero") //
		Integer height) implements ImageConverterRequestInterface {
}