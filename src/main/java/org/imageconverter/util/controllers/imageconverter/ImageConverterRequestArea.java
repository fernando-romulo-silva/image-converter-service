package org.imageconverter.util.controllers.imageconverter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public record ImageConverterRequestArea( //
		
		@NotNull(message = "The data cannot be null") //
		MultipartFile data, //
		//
		@NotNull(message = "The executionType cannot be null") //
		ExecutionType executionType, //
		//
		@Min(value = 0, message = "The x point must be greater than zero") //
		Integer x, //
		//
		@Min(value = 0, message = "The y point must be greater than zero") //
		Integer y, //
		//
		@Min(value = 0, message = "The with must be greater than zero") //
		Integer width, //
		//
		@Min(value = 0, message = "The height must be greater than zero") //
		Integer height) implements ImageConverterRequestInterface {
}