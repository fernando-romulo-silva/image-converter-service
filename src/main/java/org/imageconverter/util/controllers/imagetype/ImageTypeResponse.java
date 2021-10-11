package org.imageconverter.util.controllers.imagetype;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageTypeResponse", description = "Response structure to find Image Type")
public record ImageTypeResponse( //

		@JsonProperty(value = "id", required = true) //
		@Schema(name = "id", required = true) //
		Long id, //

		@JsonProperty(value = "extension", required = true) //
		@Schema(name = "extension", required = true) //
		String extension, //

		@JsonProperty(value = "name", required = true) //
		@Schema(name = "name", required = true) //
		String name //
) {

}
