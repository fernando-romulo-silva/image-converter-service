package org.imageconverter.util.controllers.imagetype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure to update image type.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "UpdateImageTypeRequest", description = "Resquest structure to update Image Type")
@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(Include.NON_NULL)
public record UpdateImageTypeRequest( //

		@Schema(name = "extension", required = false, example = "bmp") //
		@JsonProperty(value = "extension", required = false) //
		String extension,

		@Schema(name = "name", required = false, example = "BitMap") //
		@JsonProperty(value = "name", required = false) //
		String name, //

		@Schema(name = "description", required = false, example = "Device independent bitmap") //
		@JsonProperty(value = "description", required = false) //
		String description) {
}