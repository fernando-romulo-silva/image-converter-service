package org.imageconverter.infra.util.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@ApiResponse( //
		responseCode = "500", //
		description = "Unexpected error", //
		content = @Content( //
				mediaType = "application/json", //
				examples = @ExampleObject(//
						description = "Without flag trace or with value igual false at end of request", //
						value = """
								{
								   "timestamp":"2021-07-19T15:25:32.389836763",
								   "status":500,
								   "error":"Internal Server Error",
								   "message":"Unexpected error. Please, check the log with traceId and spanId for more detail",
								   "traceId":"3d4144eeb01e3682",
								   "spanId":"3d4144eeb01e3682"
								}
								""")))
public @interface OpenApiResponseError500 {

}
