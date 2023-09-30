package org.imageconverter.controller;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.imageconverter.infra.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imageconverter.application.ImageConversionService;
import org.imageconverter.domain.conversion.ExecutionType;
import org.imageconverter.domain.conversion.ImageConversion;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConversionPostResponse;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConversionResponse;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequest;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestArea;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.imageconverter.infra.util.logging.Loggable;
import org.imageconverter.infra.util.openapi.imageconverter.ImageConverterRestGetByIdOpenApi;
import org.imageconverter.infra.util.openapi.imageconverter.ImageConverterRestGetOpenApi;
import org.imageconverter.infra.util.openapi.imageconverter.ImageConverterRestPostAreaOpenApi;
import org.imageconverter.infra.util.openapi.imageconverter.ImageConverterRestPostOpenApi;
import org.imageconverter.infra.util.openapi.imagetype.ImageTypeRestDeleteOpenApi;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Image converter http rest.
 * 
 * @author Fernando Romulo da Silva
 */
@SecurityRequirement(name = "BASIC")
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
public class ImageConversionRestController {

    private final ImageConversionService imageConversionService;

    /**
     * Default constructor.
     * 
     * @param imageConversionService The image convert service
     */
    ImageConversionRestController(final ImageConversionService imageConversionService) {
	super();
	this.imageConversionService = imageConversionService;
    }

    /**
     * Get a conversion already done.
     * 
     * @param id The image conversion's id
     * @return A {@link ImageConversionResponse} object
     * @exception ElementNotFoundException if a element with id not found
     */
    @ImageConverterRestGetByIdOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(value = "/{id:[\\d]*}", produces = APPLICATION_JSON_VALUE)
    public ImageConversionResponse getById( //
		    @Parameter(name = "id", description = "The image conversion id's", required = true, example = "3") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	return imageConversionService.findById(id);
    }

    /**
     * Get conversions by filter.
     * 
     * @param filter A object {@link Specification} that specific the filter the search
     * @param page   A object {@link Pageable} that page the result
     * @return A {@link List} or a empty list
     */
    @PageableAsQueryParam
    @ImageConverterRestGetOpenApi
    //
    @ResponseStatus(OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Page<ImageConversionResponse> getByFilter( //
		    @Parameter(name = "filter", description = "Search's filter", required = true, example = "?filter=fileName:'image.png'") //
		    @Filter //
		    final Specification<ImageConversion> filter, // 
		    //
		    @PageableDefault(value = 10, page = 0) //
		    final Pageable page) {

	return imageConversionService.findBySpecification(filter, page);
    }

    /**
     * Convert a image file on text.
     * 
     * @param file The image to convert
     * @return A {@link ImageConversionResponse} object with response.
     */
    @ImageConverterRestPostOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    public ImageConversionPostResponse convert( //
		    @Parameter(description = "The Image to be uploaded", content = @Content(mediaType = MULTIPART_FORM_DATA_VALUE), required = true, example = "image.bmp") //
		    @RequestPart(name = "file", required = true) //
		    final MultipartFile file,
		    //
		    final HttpServletRequest request, final HttpServletResponse response) {

	final var bytes = extractBytes(file);

	final var executionType = extractExecutionType(request);

	final var result = imageConversionService.convert(new ImageConverterRequest(file.getOriginalFilename(), bytes, executionType));

	response.addHeader("Location", REST_URL + "/" + result.id());

	return new ImageConversionPostResponse(result.text());
    }

    /**
     * Convert a image file with area on text.
     * 
     * @param file   The image to convert
     * @param xAxis  The image's x coordinate
     * @param yAxis  The image's y coordinate
     * @param width  The image's width in pixels
     * @param height The image's height in pixels
     * @return A {@link ImageConversionResponse} object with response.
     */
    @ImageConverterRestPostAreaOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(value = "/area", consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    public ImageConversionPostResponse convertWithArea( //
		    @Parameter(description = "The Image to be uploaded", content = @Content(mediaType = MULTIPART_FORM_DATA_VALUE), required = true, example = "image.bmp") //
		    @RequestPart(name = "file", required = true) //
		    final MultipartFile file, //
		    //
		    @Parameter(description = "The vertical position", required = true, example = "3") //
		    @RequestParam(required = true) //
		    final Integer xAxis, //
		    //
		    @Parameter(description = "The horizontal position", required = true, example = "4") //
		    @RequestParam(required = true) //
		    final Integer yAxis, //
		    //
		    @Parameter(description = "The width size's area", required = true, example = "56") //
		    @RequestParam(required = true) //
		    final Integer width, //
		    //
		    @Parameter(description = "The height's size area ", required = true, example = "345") //
		    @RequestParam(required = true) //
		    final Integer height,
		    //
		    final HttpServletRequest request, final HttpServletResponse response) {

	final var executionType = extractExecutionType(request);

	final byte[] bytes = extractBytes(file);

	final var result = imageConversionService.convert(new ImageConverterRequestArea(file.getOriginalFilename(), bytes, executionType, xAxis, yAxis, width, height));

	response.addHeader("Location", REST_URL + "/" + result.id());

	return new ImageConversionPostResponse(result.text());
    }

    /**
     * Convert multiple images file on text.
     * 
     * @param files The images' collection to convert
     * @return A list of {@link ImageConversionResponse} with each response.
     */
    @ImageConverterRestPostOpenApi
    //
    @ResponseStatus(CREATED)
    @PostMapping(value = "/multiple", consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    public List<ImageConversionPostResponse> convert( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = MULTIPART_FORM_DATA_VALUE), required = true, example = "image1.bmp, image2.png") //
		    @RequestPart(name = "files", required = true) //
		    final MultipartFile[] files, //
		    //
		    final HttpServletRequest request, //
		    final HttpServletResponse response) {

	final var executionType = extractExecutionType(request);

	final var listRequest = new ArrayList<ImageConverterRequestInterface>();

	for (final var file : files) {
	    listRequest.add(new ImageConverterRequest(file.getOriginalFilename(), extractBytes(file), executionType));
	}

	final var result = imageConversionService.convert(listRequest);

	response.addHeader("Location", result.stream().map(rst -> REST_URL + "/" + rst.id()).collect(joining(";")));

	return result.stream().map(rst -> new ImageConversionPostResponse(rst.text())).toList();
    }

    /**
     * Delete a image conversion.
     * 
     * @param id The conversion id
     */
    @ImageTypeRestDeleteOpenApi
    //
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id:[\\d]*}")
    public void delete( //
		    //
		    @Parameter(description = "The conversion id's", example = "1000") //
		    @PathVariable(name = "id", required = true) //
		    final Long id) {

	imageConversionService.deleteImageConversion(id);
    }
    
    
    /**
     * Create a CSV file using filter.
     * 
     * @param filter A object {@link Specification} that specific the filter the search
     * @return A Csv file
     */
    @ResponseStatus(OK)
    @GetMapping(value = "/export", produces = "txt/csv")
    public ResponseEntity<InputStreamResource> downloadImageConversionCsv( //		    
		    @Parameter(name = "filter", description = "Search's filter", required = true, example = "?filter=fileName:'image.png'") //
		    @Filter //
		    final Specification<ImageConversion> filter ) {
	
	final var bytes = imageConversionService.findBySpecificationToCsv(filter);
	
	final var body = new InputStreamResource(new ByteArrayInputStream(bytes));
	
	return ResponseEntity.ok()
			.header(CONTENT_DISPOSITION, "attachment; filename=convertions.csv")
			.contentType(MediaType.parseMediaType("txt/csv"))
			.body(body);
    }
    

    private byte[] extractBytes(final MultipartFile file) {
	byte[] bytes;
	try {
	    bytes = file.getBytes();
	} catch (final IOException e) {
	    bytes = new byte[0];
	}
	return bytes;
    }

    private ExecutionType extractExecutionType(final HttpServletRequest request) {
	final var executionTypeHeader = Optional.ofNullable(request.getHeader("Execution-Type")).orElse(EMPTY);

	return ExecutionType.from(executionTypeHeader);
    }
}