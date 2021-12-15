package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imagetype.ImageTypeResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@ApiResponse( //
		responseCode = "201", //
		description = "Image type successful created", //
		content = { //
			@Content(mediaType = "application/json", schema = @Schema(implementation = ImageTypeResponse.class)) //
		} //
)
public @interface OpenApiResponse201 {

}
