package org.imageconverter.util.controllers.imageconverter;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response to http methods for image convertion.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageCon" + "verterResponse", description = "Response to convert image")
public record ImageConverterResponse( //
		//
		@Schema(name = "id", required = true, example = "1") //
		@JsonProperty(value = "id", required = true) //
		Long id,
		//
		@Schema(name = "file_name", required = true, example = "image.bmp") //
		@JsonProperty(value = "file_name", required = true) //
		String fileName,
		//
		@Schema(name = "txt", required = true, example = "2343 3455") //
		@JsonProperty(value = "text", required = true) //
		String text) {
}