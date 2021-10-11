package org.imageconverter.util.controllers.imagetype;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = false)
@Schema(name = "ImageTypeRequest", description = "Resquest structure to create Image Type")
public record CreateImageTypeRequest( //

		@Schema(name = "extension", required = true) //
		@JsonProperty(value = "extension", required = true) //
		@NotEmpty(message = "The 'extension' cannot be empty") //
		String extension,

		@Schema(name = "name", required = true) //
		@JsonProperty(value = "name", required = true) //
		@NotEmpty(message = "The 'name' cannot be empty") //
		String name, //

		@Schema(name = "description", required = false) //
		@JsonProperty(value = "description", required = false) //
		String description) {
}
