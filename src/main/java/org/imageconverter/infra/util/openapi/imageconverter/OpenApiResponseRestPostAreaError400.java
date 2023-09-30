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
				examples = { //
					@ExampleObject(//
							name = "Example 1",
							description = "Some parameter missing", //
							value = """
									{
									   "timestamp":"2021-12-13T14:13:57.716891867",
									   "status":400,
									   "error":"Bad Request",
									   "message":"MissingServletRequestParameterException: The parameter 'height' is missing",
									   "traceId":"25ff4608ea900f8d",
									   "spanId":"25ff4608ea900f8d"
									}
																				"""), //

					@ExampleObject(//
							name = "Example 2",
							description = "Invalid value on parameter", //
							value = """
									{
									   "timestamp":"2021-12-13T14:27:13.243520325",
									   "status":400,
									   "error":"Bad Request",
									   "message":"The y point must be greater than zero",
									   "traceId":"c9f59ea5f02d8356",
									   "spanId":"c9f59ea5f02d8356"
									}
																				""") //

				}))
@interface OpenApiResponseRestPostAreaError400 {

}
