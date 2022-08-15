package org.imageconverter.util.openapi.imageconverter;

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
		responseCode = "404", //
		description = "Not found", //
		content = @Content(//
				mediaType = "application/json", //
				examples = @ExampleObject(//
						description = "Image Conversion not found", //
						value = """
								{
								    "timestamp": "2021-12-05T20:18:41.80433145",
								    "status": 404,
								    "error": "Not Found",
								    "message": "ElementNotFoundException: ImageConversion with id '1' not found",
								    "traceId": "1e3398a945ef0d79",
								    "spanId": "1e3398a945ef0d79"
								}
																""")))
@interface OpenApiResponseError404 {

}
