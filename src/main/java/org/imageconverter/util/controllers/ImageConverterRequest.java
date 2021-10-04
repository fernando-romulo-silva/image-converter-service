package org.imageconverter.util.controllers;

import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imageConvertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public record ImageConverterRequest( //
		@NotNull(message = "The data cannot be null") //
		MultipartFile data, //
		//
		@NotNull(message = "The executionType cannot be null") //
		ExecutionType executionType) implements ImageConverterRequestInterface {

}
