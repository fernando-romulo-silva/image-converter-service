package org.imageconverter.infra.util.controllers.jsonpatch;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structure to execute patch operations.
 * 
 * @author Fernando Romulo da Silva
 */
@Schema(name = "JsonPatch", description = "Response to convert image")
public record JsonPatch(
		@Schema(name = "op", required = true, example = "replace, remove") //
		@JsonProperty(value = "op", required = true) //		
		JsonPatchOperation op,

		@Schema(name = "path", required = true, example = "name") //
		@JsonProperty(value = "path", required = true) //	
		String path,

		@Schema(name = "value", required = true, example = "Paul") //
		@JsonProperty(value = "value", required = false) //	
		String value) {
}
