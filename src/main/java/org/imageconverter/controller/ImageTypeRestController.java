package org.imageconverter.controller;

import static org.imageconverter.infra.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.imageconverter.application.ImageTypeService;
import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.util.controllers.imagetype.ImageTypeRequest;
import org.imageconverter.infra.util.controllers.imagetype.ImageTypeResponse;
import org.imageconverter.infra.util.controllers.jsonpatch.JsonPatch;
import org.imageconverter.infra.util.logging.Loggable;
import org.imageconverter.infra.util.openapi.imagetype.CreateImageTypeRequestBody;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestDeleteOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestGet;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestGetByIdOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestPatchOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestPostOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestPutOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.UpdateImageTypeRequestBody;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Image type CRUD.
 * 
 * @author Fernando Romulo da Silva
 */
@SecurityRequirement(name = "BASIC")
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
@RestController
@Description("Controller for image type API")
@RequestMapping(REST_URL)
public class ImageTypeRestController {

    private final ImageTypeService imageTypeService;

    /**
     * Default constructor.
     * 
     * @param imageConversionService The image type service
     */
    ImageTypeRestController(final ImageTypeService imageTypeService) {
	super();
	this.imageTypeService = imageTypeService;
    }

    /**
     * Get a image type by id.
     * 
     * @param id The image type's id
     * @return A {@link ImageTypeResponse} object
     * @exception ElementNotFoundException if a element with id not found
     */
    @ImageTypeRestGetByIdOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(value = "/{id:[\\d]*}", produces = APPLICATION_JSON_VALUE)
    public ImageTypeResponse getById( //
		    @Parameter(description = "The image type id's", example = "1000") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	return imageTypeService.findById(id);
    }

    /**
     * Get image types.
     * 
     * @param filter A object {@link Specification} that specific the filter the search, if you omit, bring all 
     * @param page   A object {@link Pageable} that page the result
     * @return A {@link List} or a empty list
     */
    @PageableAsQueryParam
    @ImageTypeRestGet
    //
    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Page<ImageTypeResponse> getByFilter( //
		    @Parameter(name = "filter", description = "Search's filter", required = true, example = "?filter=extension:'png'") //
		    @Filter //
		    final Specification<ImageType> filter, //
		    //
		    @PageableDefault(value = 10, page = 0)
		    final Pageable page) {

	return imageTypeService.findBySpecification(filter, page);
    }

    /**
     * Create a image type.
     * 
     * @param request A {@link ImageTypeRequest} object. It works as a structure to create a {@link org.imageconverter.domain.imagetype.ImageType}
     * @return A string with image type's id
     */
    @ImageTypeRestPostOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = { TEXT_PLAIN_VALUE, APPLICATION_JSON_VALUE })
    public void create(//
		    //
		    @CreateImageTypeRequestBody //
		    @Valid //
		    @RequestBody //
		    final ImageTypeRequest request,
		    //
		    final HttpServletResponse response) {

	final var result = imageTypeService.createImageType(request);
	
	response.addHeader("Location", REST_URL + "/" + result.id());
    }

    /**
     * Update a whole image type.
     * 
     * @param id      The image type's id
     * 
     * @param request A {@link ImageTypeRequest} object. It works as a structure to update a {@link org.imageconverter.domain.imagetype.ImageType}
     */
    @ImageTypeRestPutOpenApi
    //
    @ResponseStatus(NO_CONTENT)
    @PutMapping(value = "/{id:[\\d]*}", consumes = APPLICATION_JSON_VALUE)
    public void update( //
		    //
		    @Parameter(description = "The image type id's", example = "1000") //
		    @PathVariable(name = "id", required = true) //
		    final Long id, //

		    @UpdateImageTypeRequestBody //
		    @Valid //
		    @RequestBody //
		    final ImageTypeRequest request) {

	imageTypeService.updateImageType(id, request);
    }

    /**
     * Update partially a image type.
     * 
     * @param id      The image type's id
     * @param request A {@link JsonPatch} object to update the object
     */
    @ImageTypeRestPatchOpenApi
    //
    @ResponseStatus(NO_CONTENT)
    @PatchMapping(path = "/{id}", consumes = "application/json")
    public void update(		    
		    @Parameter(description = "The image type id's", example = "1000") //
    		    @PathVariable(name = "id", required = true) //
		    final Long id, 
		    
		    @Parameter(description = "A json path structure", 
		    	       example = """
		     				 [
		     				   { 
		     				     "op":"replace",
		     				     "path":"name",
		     				     "value":"new name"
		     				   },
		     				   { 
		     				     "op":"remove",
		     				     "path":"name"
		     				   }		     				   
		     				 ]
		     				""") //
		    @RequestBody 
		    final List<JsonPatch> jsonPatchs) {

	imageTypeService.updateImageType(id, jsonPatchs);
    }   

    /**
     * Delete a image type.
     * 
     * @param id The image type's id
     */
    @ImageTypeRestDeleteOpenApi
    //
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id:[\\d]*}")
    public void delete( //
		    //
		    @Parameter(description = "The image type id's", example = "1000") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	imageTypeService.deleteImageType(id);
    }
}
