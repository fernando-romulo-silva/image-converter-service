package org.imageconverter.infra.util.openapi.imageconverter;

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
@Operation(summary = "Convert all image into the text", description = "This method convert a whole file, may it'll take a long time")
//
@OpenApiResponse201
@org.imageconverter.infra.util.openapi.imagetype.OpenApiResponseError404
@OpenApiResponseRestPostError409
@OpenApiResponseError500
public @interface ImageConverterRestPostOpenApi {

}
