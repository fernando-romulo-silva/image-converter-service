package org.imageconverter.controller.rest;

import static org.imageconverter.domain.ExecutionType.WS;
import static org.imageconverter.util.controllers.ImageConverterConst.REST_URL;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import org.imageconverter.application.ImageConversionService;
import org.imageconverter.util.controllers.ImageConverterRequest;
import org.imageconverter.util.controllers.ImageConverterResponse;
import org.imageconverter.util.logging.Loggable;
import org.imageconverter.util.openapi.ApiResponseError500;
import org.imageconverter.util.openapi.imageconverter.ApiResponse201;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Description("Controller for image converstion API")
@RequestMapping(value = REST_URL)
//
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
class ImageConverterRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ImageConversionService imageConversionService;

    public ImageConverterRestController(final ImageConversionService imageConversionService) {
	super();
	this.imageConversionService = imageConversionService;
    }

    @Operation(summary = "Convert all image into the text")
    @ApiResponse201
    @ApiResponseError500
    //
    @ResponseStatus(CREATED)
    @PostMapping(consumes = { "multipart/form-data" }, produces = "application/json")
    public ImageConverterResponse convert( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final MultipartFile file) {

	final var kilobytes = (file.getSize() / 1024);

	logger.info("begin with image {}, size {}", file.getName(), kilobytes);

	final var result = imageConversionService.convert(new ImageConverterRequest(file, WS));

	logger.info("end with result {}", result);

	return result;

    }

    @Operation(summary = "Convert the image with specific area")
    @ApiResponse201
    @ApiResponseError500
    //
    @ResponseStatus(CREATED)
    @PostMapping(value = "/area", consumes = { "multipart/form-data" }, produces = "application/json")
    public ImageConverterResponse convertWithArea( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true, schema = @Schema(allowableValues = "> 0")) //
		    @RequestParam(required = true) //
		    final MultipartFile file, //
		    //
		    @Parameter(description = "The vertical position", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = false) //
		    final int x, //
		    //
		    @Parameter(description = "The horizontal position", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = false) //
		    final int y, //
		    //
		    @Parameter(description = "The width size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = false) //
		    final int width, //
		    //
		    @Parameter(description = "The height size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = false, defaultValue = "0") //
		    final int height) {

	final var kilobytes = (file.getSize() / 1024);

	logger.info("Image with size {}", file.getName(), kilobytes, x, y, width, height);

	final var result = imageConversionService.convert(new ImageConverterRequest(file, WS, x, y, width, height));

	return result;

    }

    @Operation(summary = "Convert the image with specific area")
    @GetMapping("/ping")
    @ResponseStatus(OK)
    public String ping() {
	return "ping!";
    }
}