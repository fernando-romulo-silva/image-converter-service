package org.imageconverter.util.controllers.imageconverter;

import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ExecutionType;
import org.springframework.web.multipart.MultipartFile;

public record ImageConverterRequest( //
		@NotNull(message = "The 'data' cannot be null") //
		MultipartFile data, //
		//
		@NotNull(message = "The 'executionType' cannot be null") //
		ExecutionType executionType) implements ImageConverterRequestInterface {
}
