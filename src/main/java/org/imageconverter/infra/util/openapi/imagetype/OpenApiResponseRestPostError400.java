package org.imageconverter.infra.util.openapi.imagetype;

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
							name = "Example 1", description = "Image Type invalid property", //
							value = """
									{
									   "timestamp":"2021-12-19T10:27:49.484938769",
									   "status":400,
									   "error":"Bad Request",
									   "message":"MismatchedInputException: Missing required creator property 'extension' ",
									   "traceId":"b3739a0b6c59eab3",
									   "spanId":"ecba3385978800d1"
									}
																				"""), //

					@ExampleObject(//
							name = "Example 2", description = "Image type with invalid values property", //
							value = """
									{
									   "timestamp":"2021-12-19T10:31:35.544918342",
									   "status":400,
									   "error":"Bad Request",
									   "message":"The 'name' cannot be empty",
									   "traceId":"1d50da5635af8f66",
									   "spanId":"78a329966f192e5a"
									}
																				""") //

				}))
@interface OpenApiResponseRestPostError400 {

}
