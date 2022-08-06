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
//
@ApiResponse(//
		responseCode = "409", //
		description = "Conflict", //
		content = @Content(//
				mediaType = "application/json", //
				examples = @ExampleObject(//
						description = "Image Convertion type with file name already exists", //
						value = """
									{
									  "timestamp": "2021-12-12T10:47:36.398207503",
									  "status": 409,
									  "error": "Conflict",
									  "message": "ElementAlreadyExistsException: ImageConvertion with fileName 'somefile.png' and with text '0339990574' already exists", 
									  "traceId": "afab49eeb47d3660",
									  "spanId": "afab49eeb47d3660"
									}
								""")))
@interface OpenApiResponseRestPostError409 {

}
