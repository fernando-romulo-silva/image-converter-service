package org.imageconverter.controller;

import static org.imageconverter.domain.convertion.ExecutionType.WS;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.imageconverter.application.ImageConversionService;
import org.imageconverter.domain.convertion.ImageConvertion;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequest;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea;
import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;
import org.imageconverter.util.logging.Loggable;
import org.imageconverter.util.openapi.imageconverter.ImageConverterRestGetAllOpenApi;
import org.imageconverter.util.openapi.imageconverter.ImageConverterRestGetByIdOpenApi;
import org.imageconverter.util.openapi.imageconverter.ImageConverterRestGetBySearchOpenApi;
import org.imageconverter.util.openapi.imageconverter.ImageConverterRestPostAreaOpenApi;
import org.imageconverter.util.openapi.imageconverter.ImageConverterRestPostOpenApi;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag( //
	name = "Image Convert", //
	description = """
			Image Convert API - If something went wrong, please put 'trace' (for all Http methods)
			at the end of the call to receive the stackStrace.
			Ex: http://127.0.0.1:8080/image-converter/rest/images/convert?trace=true
			     """ //
)
//
@Loggable
@RestController
@Description("Controller for image converstion API")
@RequestMapping(REST_URL)
public class ImageConverterRestController {

    private final ImageConversionService imageConversionService;

    ImageConverterRestController(final ImageConversionService imageConversionService) {
	super();
	this.imageConversionService = imageConversionService;
    }

    @ImageConverterRestGetByIdOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(value = "/{id:[\\d]*}", produces = APPLICATION_JSON_VALUE)
    public ImageConverterResponse show( //
		    @Parameter(description = "The image conversion id's") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	return imageConversionService.findById(id);
    }

    @ImageConverterRestGetAllOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<ImageConverterResponse> showAll() {

	return imageConversionService.findAll();
    }

    @ImageConverterRestGetBySearchOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(value = "/search", produces = APPLICATION_JSON_VALUE)
    public List<ImageConverterResponse> show( //
		    @Parameter(name = "filter", description = "Search's filter", required = true, example = "/search?filter=fileName:'image.png'")
		    @Filter //
		    final Specification<ImageConvertion> filter, final Pageable page) {

	return imageConversionService.findBySpecification(filter);
    }

    @ImageConverterRestPostOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(consumes = { "multipart/form-data" }, produces = "application/json")
    public ImageConverterResponse convert( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(name = "file", required = true) //
		    final MultipartFile file) {

	return imageConversionService.convert(new ImageConverterRequest(file, WS));
    }

    @ImageConverterRestPostAreaOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(value = "/area", consumes = { "multipart/form-data" }, produces = "application/json")
    public ImageConverterResponse convertWithArea( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true, schema = @Schema(allowableValues = "> 0")) //
		    @RequestParam(value = "file", required = true) //
		    final MultipartFile file, //
		    //
		    @Parameter(description = "The vertical position", required = true) //
		    @RequestParam(required = true) //
		    final Integer x, //
		    //
		    @Parameter(description = "The horizontal position", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final Integer y, //
		    //
		    @Parameter(description = "The width size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final Integer width, //
		    //
		    @Parameter(description = "The height size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final Integer height) {

	return imageConversionService.convert(new ImageConverterRequestArea(file, WS, x, y, width, height));
    }
}