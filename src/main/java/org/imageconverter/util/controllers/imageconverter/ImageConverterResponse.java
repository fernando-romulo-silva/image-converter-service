package org.imageconverter.util.controllers.imageconverter;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageConverterResponse", description = "Response to convert image")
public final record ImageConverterResponse( //
		//
		@JsonProperty(value = "id", required = true) //
		@Schema(name = "id", required = true) //
		Long id,
		//
		@JsonProperty(value = "file_name", required = true) //
		@Schema(name = "file_name", required = true) //
		String fileName,
		//
		@JsonProperty(value = "text", required = true) //
		@Schema(name = "txt", required = true) //
		String text //
) {
}