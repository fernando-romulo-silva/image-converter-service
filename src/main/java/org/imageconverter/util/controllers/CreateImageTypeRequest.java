package org.imageconverter.util.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageTypeRequest", description = "Resquest structure to create Image Type")
public record CreateImageTypeRequest( //

		@JsonProperty(value = "extension", required = true) //
		@Schema(name = "extension", required = true) //
		String extension,

		@JsonProperty(value = "name", required = true) //
		@Schema(name = "name", required = true) //
		String name, //

		@JsonProperty(value = "name", required = false) //
		@Schema(name = "name", required = false) //
		String description
) //
{

}
