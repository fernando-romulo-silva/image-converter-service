package org.imageconverter.util.openapi.imageconverter;

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
		summary = "Convert the image with specific area", //
		description = "This method convert a file's region, it'll maybe not take a long time" //
)
@OpenApiResponse201
@org.imageconverter.util.openapi.imagetype.OpenApiResponseError404
@OpenApiResponseRestPostAreaError400
@OpenApiResponseRestPostError409
@OpenApiResponseError500
public @interface ImageConverterRestPostAreaOpenApi {

}
