package org.imageconverter.util.controllers;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = false)
@Schema(name = "ImageTypeRequest", description = "Resquest structure to create Image Type")
public record CreateImageTypeRequest( //

		@JsonProperty(value = "extension", required = true) //
		@Schema(name = "extension", required = true) //
		@NotEmpty(message = "Extension cannot be empty") //
		String extension,

		@JsonProperty(value = "name", required = true) //
		@Schema(name = "name", required = true) //
		@NotEmpty(message = "Name cannot be empty") // 
		String name, //

		@JsonProperty(value = "description", required = false) //
		@Schema(name = "description", required = false) //
		String description) //
{

}
