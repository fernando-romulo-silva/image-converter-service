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
		summary = "Update a image's type", //
		description = "This method update a new image's type" //
)
@OpenApiResponseRestUpdate204
@OpenApiResponseRestPostError400
@OpenApiResponseError404
@OpenApiResponseError500
public @interface ImageTypeRestPutOpenApi {

}
