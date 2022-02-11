package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation( //
		summary = "Delete a image's type", //
		description = "This method delete a new image's type" //
)
@OpenApiResponseRestDelete204
@OpenApiResponseError404
@OpenApiResponseRestPostError409 
@OpenApiResponseError500
public @interface ImageTypeRestDeleteOpenApi {

}
