package org.imageconverter.util.openapi.imageconverter;

import org.imageconverter.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;

@Operation(summary = "Convert the image with specific area", description = "This method convert a file's region, it'll maybe not take a long time")
@OpenApiResponse201
@OpenApiResponseError500
public @interface ImageConverterRestPostAreaOpenApi {

}
