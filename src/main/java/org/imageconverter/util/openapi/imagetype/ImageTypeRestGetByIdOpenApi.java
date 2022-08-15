package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imageconverter.ImageConversionResponse;
import org.imageconverter.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation(summary = "Get a ImageConversion by id", description = "This method try to find a imageConversion by id")
@ApiResponse( //
		responseCode = "200", //
		description = "Image conversion found", //
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageConversionResponse.class)) //
)
//
@OpenApiResponseError404
@OpenApiResponseError500
public @interface ImageTypeRestGetByIdOpenApi {

}
