package org.imageconverter.controller.rest;

import static java.text.MessageFormat.format;
import static org.imageconverter.util.controllers.ImageTypeConst.REST_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import javax.validation.Valid;

import org.imageconverter.application.ImageTypeService;
import org.imageconverter.util.controllers.CreateImageTypeRequest;
import org.imageconverter.util.controllers.UpdateImageTypeRequest;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Description("Controller for image type API")
@RequestMapping(value = REST_URL)
@Tag( //
		name = "Image Type", //
		description = """
				Image Type API - If something went wrong, please put 'trace' (for all Http methods)
				at the end of the call to receive the stackStrace.
				Ex: http://127.0.0.1:8080/image-converter/rest/images/convert?trace=true
				     """ //
)
//
@Loggable
public class ImageTypeRestController {

    private final ImageTypeService imageTypeService;

    @Autowired
    public ImageTypeRestController(final ImageTypeService imageTypeService) {
	super();
	this.imageTypeService = imageTypeService;
    }

    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = { TEXT_PLAIN_VALUE, APPLICATION_JSON_VALUE })
    public String create(//

		    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A new image type", content = @Content(schema = @Schema(implementation = CreateImageTypeRequest.class))) //
		    @Valid //
		    @RequestBody //
		    final CreateImageTypeRequest request) {

	final var result = imageTypeService.createImageType(request);

	return format("Image Type ''{0}'' created", result.id());
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = { TEXT_PLAIN_VALUE, APPLICATION_JSON_VALUE })
    public String update( //
		    @Parameter(description = "The image type id's") //
		    final Long id, //

		    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A existed image type", content = @Content(schema = @Schema(implementation = UpdateImageTypeRequest.class))) //
		    @Valid //
		    @RequestBody //
		    final UpdateImageTypeRequest request) {

	final var result = imageTypeService.updateImageType(id, request);

	return format("Image Type ''{0}'' updated", result.id());
    }

    @ResponseStatus(OK)
    @GetMapping(value = "/{id:[\\d]*}")
    public void show(@PathVariable final Long id) {

    }

    @GetMapping("/ping")
    @ResponseStatus(OK)
    public String ping() {
	return "ping!";
    }
}
