package org.imageconverter.infra.util.openapi.imageconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.infra.util.controllers.imageconverter.ImageConversionResponse;
import org.imageconverter.infra.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation( //
		summary = "Get image conversions by criteria search", //
		description = "This method return image conversions that satisfy a certain condition" //
)
@ApiResponse( //
		responseCode = "200", //
		description = "Image conversions found or a empty array if didn't find anything", //
		content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ImageConversionResponse.class))) //
)
//
@OpenApiResponseError500
@OpenApiResponseGetBySearchError400
public @interface ImageConverterRestGetOpenApi {

}
