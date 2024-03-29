package org.imageconverter.infra.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.infra.util.controllers.imagetype.ImageTypeRequest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@RequestBody( //
		description = "A new image type", //
		content = @Content( //
				schema = @Schema(implementation = ImageTypeRequest.class) //
		) //
) //
public @interface CreateImageTypeRequestBody {

}
