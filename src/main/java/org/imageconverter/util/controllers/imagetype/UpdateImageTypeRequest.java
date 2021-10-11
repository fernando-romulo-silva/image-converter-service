package org.imageconverter.util.controllers.imagetype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateImageTypeRequest", description = "Resquest structure to update Image Type")
@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(Include.NON_NULL)
public record UpdateImageTypeRequest( //

		@JsonProperty(value = "extension", required = false) //
		@Schema(name = "extension", required = false) //
		String extension,

		@JsonProperty(value = "name", required = false) //
		@Schema(name = "name", required = false) //
		String name, //

		@JsonProperty(value = "description", required = false) //
		@Schema(name = "description", required = false) //
		String description) //
{

    
    public UpdateImageTypeRequest {
//        Objects.requireNonNull(id);
        // ... 
    }
}
