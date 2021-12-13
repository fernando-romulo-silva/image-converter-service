package org.imageconverter.util.controllers.imagetype;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageTypeResponse", description = "Response structure to find Image Type")
public record ImageTypeResponse( //

		@Schema(name = "id", description = "The image type identification",  required = true, example = "3") //
		@JsonProperty(value = "id", required = true) //
		Long id, //

		@Schema(name = "extension", description = "The image type extension", required = true, example = "bmp") //
		@JsonProperty(value = "extension", required = true) //
		String extension, //

		@Schema(name = "name", description = "The image type name", required = true, example = "BitMap") //
		@JsonProperty(value = "name", required = true) //
		String name) {

}