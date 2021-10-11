package org.imageconverter.util.openapi.imagetype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.imageconverter.util.controllers.imagetype.UpdateImageTypeRequest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
//
@RequestBody( //
		description = "A existed image type", //
		content = @Content( //
				schema = @Schema(implementation = UpdateImageTypeRequest.class) //
		) //
) //
public @interface UpdateImageTypeRequestBody {

}
