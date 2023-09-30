package org.imageconverter.infra.util.openapi.imageconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ApiResponse(//
		responseCode = "400", //
		description = "Bad Request", //
		content = @Content(//
				mediaType = "application/json", //
				examples = @ExampleObject(//
						description = "Image Conversion not found", //
						value = """
								{
								    "timestamp": "2021-12-12T10:47:36.398207503",
								    "status": 400,
								    "error": "Not Found",
								    "message": "ElementInvalidException: Unable to locate Attribute with the the given name 'invalidField' on ImageConversion",
								    "traceId": "afab49eeb47d3660",
								    "spanId": "afab49eeb47d3660"
								}
										""")))
@interface OpenApiResponseGetBySearchError400 {

}
