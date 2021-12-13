package org.imageconverter.util.openapi.imagetype;

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
//
@ApiResponse(//
		responseCode = "400", //
		description = "Bad Request", //
		content = @Content(//
				mediaType = "application/json", //
				examples = { //
					@ExampleObject(//
							description = "Image Convertion not found", //
							value = """
									{
									    "timestamp": "2021-12-12T10:47:36.398207503",
									    "status": 400,
									    "error": "Bad Request",
									    "message": "ElementInvalidException: Unable to locate Attribute with the the given name 'invalidField' on ImageConvertion",
									    "traceId": "afab49eeb47d3660",
									    "spanId": "afab49eeb47d3660"
									}
											"""), //

					@ExampleObject(//
							description = "Image Convertion not found", //
							value = """
									{
									    "timestamp": "2021-12-12T10:47:36.398207503",
									    "status": 400,
									    "error": "Bad Request",
									    "message": "ElementInvalidException: Unable to locate Attribute with the the given name 'invalidField' on ImageConvertion",
									    "traceId": "afab49eeb47d3660",
									    "spanId": "afab49eeb47d3660"
									}
											""") //

				}))
@interface OpenApiResponseRestPostError400 {

}
