package org.imageconverter.util.openapi.imageconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@SecurityRequirement(name = "BASIC")
@ApiResponse( //
		responseCode = "201", //
		description = "successful converte image", //
		content = { //
			@Content(mediaType = "application/json", schema = @Schema(implementation = ImageConverterResponse.class)) //
		} //
)
public @interface ApiResponse201 {

}
