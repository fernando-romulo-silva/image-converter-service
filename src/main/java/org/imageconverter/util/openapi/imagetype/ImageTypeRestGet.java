package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imagetype.ImageTypeResponse;
import org.imageconverter.util.openapi.OpenApiResponseError500;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@Operation( //
		summary = "Get image types by criteria search", //
		description = "This method return image conversions that satisfy a certain condition" //
)
@ApiResponse( //
		responseCode = "200", //
		description = "Image types found or a empty array if didn't find anything", //
		content = @Content( //
				mediaType = "application/json", //
				array = @ArraySchema(schema = @Schema(implementation = ImageTypeResponse.class)), //
				examples = @ExampleObject(value = """
						{
						    "content": [
						        {
						            "id": 1,
						            "extension": "png",
						            "name": "PNG"
						        }
						    ],
						    "pageable": {
						        "sort": {
						            "empty": true,
						            "sorted": false,
						            "unsorted": true
						        },
						        "offset": 0,
						        "pageNumber": 0,
						        "pageSize": 10,
						        "paged": true,
						        "unpaged": false
						    },
						    "totalPages": 1,
						    "totalElements": 1,
						    "last": true,
						    "size": 10,
						    "number": 0,
						    "sort": {
						        "empty": true,
						        "sorted": false,
						        "unsorted": true
						    },
						    "first": true,
						    "numberOfElements": 1,
						    "empty": false
						} """) //

		) //
)
//
@OpenApiResponseError500
@OpenApiResponseGetBySearchError400
public @interface ImageTypeRestGet {

}
