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
		responseCode = "409", //
		description = "Conflict", //
		content = @Content(//
				mediaType = "application/json", //
				examples = @ExampleObject(//
						description = "Image type with extension already exists", //
						value = """
								{
								   "timestamp":"2021-12-19T10:20:52.208177754",
								   "status":409,
								   "error":"Conflict",
								   "message":"ElementAlreadyExistsException: ImageType with extension 'BMP' already exists",
								   "traceId":"82a11390bfa08d1d",
								   "spanId":"e133d34a5560a0c0"
								}
																""")))
@interface OpenApiResponseRestDeleteError409 {

}
