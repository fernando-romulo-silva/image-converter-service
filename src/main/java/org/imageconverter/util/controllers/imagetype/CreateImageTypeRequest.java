package org.imageconverter.util.controllers.imagetype;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = false)
@Schema(name = "ImageTypeRequest", description = "Resquest structure to create Image Type")
public record CreateImageTypeRequest( //
		//
		@Schema(name = "extension", description = "The image type's extension", required = true, example = "bmp") //
		@JsonProperty(value = "extension", required = true) //
		@NotEmpty(message = "The 'extension' cannot be empty") //
		String extension,
		//
		@Schema(name = "name", description = "The image type's name", required = true, example = "BitMap") //
		@JsonProperty(value = "name", required = true) //
		@NotEmpty(message = "The 'name' cannot be empty") //
		String name, //
		//
		@Schema(name = "description", description = "A description about the image type", required = false, example = "Device independent bitmap") //
		@JsonProperty(value = "description", required = false) //
		String description) {
}