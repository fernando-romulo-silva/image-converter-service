package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@ApiResponse( //
		responseCode = "204", //
		description = "Image type successful updated" //
)
@interface OpenApiResponseRestUpdate204 {

}
