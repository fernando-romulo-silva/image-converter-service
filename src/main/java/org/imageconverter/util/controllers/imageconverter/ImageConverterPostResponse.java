package org.imageconverter.util.controllers.imageconverter;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response to post (create) convert image operation.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "ImageConverterPostResponse", description = "Response to convert image")
public record ImageConverterPostResponse( //
		
		@Schema(name = "txt", required = true, example = "2343 3455") //
		@JsonProperty(value = "text", required = true) //
		String text) {
}