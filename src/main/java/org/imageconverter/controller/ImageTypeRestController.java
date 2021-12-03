package org.imageconverter.controller;

import static java.text.MessageFormat.format;
import static org.imageconverter.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.imageconverter.application.ImageTypeService;
import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.util.controllers.imagetype.CreateImageTypeRequest;
import org.imageconverter.util.controllers.imagetype.ImageTypeResponse;
import org.imageconverter.util.controllers.imagetype.UpdateImageTypeRequest;
import org.imageconverter.util.logging.Loggable;
import org.imageconverter.util.openapi.imagetype.CreateImageTypeRequestBody;
import org.imageconverter.util.openapi.imagetype.UpdateImageTypeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Description("Controller for image type API")
@RequestMapping(REST_URL)
//
@Tag( //
		name = "Image Type", //
		description = """
				Image Type API - Rest CRUD for image type :) .
				If something went wrong, please put 'trace=true' (for all Http methods) at the end of the request to receive the stackStrace.
				Ex: http://127.0.0.1:8080/rest/images/type?trace=true
				     """ //
)
//
@Loggable
public class ImageTypeRestController {

    private final ImageTypeService imageTypeService;

    @Autowired
    ImageTypeRestController(final ImageTypeService imageTypeService) {
	super();
	this.imageTypeService = imageTypeService;
    }

    @GetMapping("/ping")
    @ResponseStatus(OK)
    public String ping() {
	return "ping!";
    }

    @ResponseStatus(OK)
    @GetMapping(value = "/{id:[\\d]*}", produces = APPLICATION_JSON_VALUE)
    public ImageTypeResponse show( //
		    @Parameter(description = "The image type id's") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	return imageTypeService.findById(id);
    }

    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<ImageTypeResponse> showAll() {

	return imageTypeService.findAll();
    }

    @ResponseStatus(OK)
    @GetMapping(value = "/search", produces = APPLICATION_JSON_VALUE)
    public List<ImageTypeResponse> get( //
		    @Filter //
		    final Specification<ImageType> spec, final Pageable page) {

	return imageTypeService.findBySpecification(spec);
    }

    @Operation(summary = "Create a new image type")
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = { TEXT_PLAIN_VALUE, APPLICATION_JSON_VALUE })
    public String create(//
		    //
		    @CreateImageTypeRequestBody //
		    @Valid //
		    @RequestBody //
		    final CreateImageTypeRequest request) {

	final var result = imageTypeService.createImageType(request);

	return format("Image Type ''{0,number,#}'' created", result.id());
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id:[\\d]*}", consumes = APPLICATION_JSON_VALUE)
    public void update( //
		    //
		    @Parameter(description = "The image type id's") //
		    @PathVariable(name = "id", required = true) //
		    final Long id, //

		    @UpdateImageTypeRequestBody //
		    @Valid //
		    @RequestBody //
		    final UpdateImageTypeRequest request) {

	imageTypeService.updateImageType(id, request);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id:[\\d]*}")
    public void delete( //
		    //
		    @Parameter(description = "The image type id's") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	imageTypeService.deleteImageType(id);
    }
}
