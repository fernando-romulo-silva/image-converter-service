package org.imageconverter.infra.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.infra.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation( //
		summary = "Update a partial image's type", //
		description = "This method update a image's type part" //
)
@OpenApiResponseRestUpdate204
@OpenApiResponseRestPostError400
@OpenApiResponseError404
@OpenApiResponseError500
public @interface ImageTypeRestPatchOpenApi {

}
