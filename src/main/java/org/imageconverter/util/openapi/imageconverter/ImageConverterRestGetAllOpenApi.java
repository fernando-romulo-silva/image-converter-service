package org.imageconverter.util.openapi.imageconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;
import org.imageconverter.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation(summary = "Get all image convertions", description = "This method return all image convertion, watch out with it")
@ApiResponse( //
		responseCode = "200", //
		description = "Image convertions found or a empty array if didn't find anything", //
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ImageConverterResponse.class))) //
)
@OpenApiResponseError500
public @interface ImageConverterRestGetAllOpenApi {

}
