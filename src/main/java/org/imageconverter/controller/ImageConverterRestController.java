package org.imageconverter.controller;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.imageconverter.domain.ExecutionType.WS;
import static org.imageconverter.util.ImageConverterConst.REST_URL;
import static org.springframework.http.HttpStatus.OK;

import org.imageconverter.application.ImageConversionService;
import org.imageconverter.util.ImageConverterRequest;
import org.imageconverter.util.ImageConverterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Description("Controller for API users")
@RequestMapping(value = REST_URL)
@Tag( //
		name = "Image Convert", //
		description = """
				Image Convert API - If something went wrong, please put 'trace' (for all Http methods)
				at the end of the call to receive the stackStrace.
				Ex: http://127.0.0.1:8080/image-converter/rest/images/convert?trace=true
				     """ //
)
final class ImageConverterRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ImageConversionService imageConversionService;

    @Autowired
    public ImageConverterRestController(final ImageConversionService imageConversionService) {
	super();
	this.imageConversionService = imageConversionService;
    }

    @Operation(summary = "Convert all image into the text")
    @ApiResponse( //
		    responseCode = "200", //
		    description = "conversion susessful", //
		    content = @Content( //
				    mediaType = "application/json", //
				    schema = @Schema(implementation = ImageConverterResponse.class)))

    @ApiResponse( //
		    responseCode = "500", //
		    description = "Error to use tesseract", //
		    content = @Content( //
				    mediaType = "application/json", //
				    examples = @ExampleObject(//
						    description = "Without flag trace or with value igual false at end of request", //
						    value = """
						    		{
						    		   "timestamp":"2021-07-19T15:25:32.389836763",
						    		   "status":500,
						    		   "error":"Internal Server Error",
						    		   "message":"Unexpected error. Please, check the log with traceId and spanId for more detail",
						    		   "traceId":"3d4144eeb01e3682",
						    		   "spanId":"3d4144eeb01e3682"
						    		}
						    		""")))

    @ApiResponse(responseCode = "404", //
		    description = "Not found", //
		    content = @Content(//
				    mediaType = "application/json", //
				    examples = @ExampleObject(//
						    value = // "{\"success\":false,\"message\":\"That shop or API version is not accessible yet\",\"httpStatus\":\"NOT_FOUND\"}")
						    """

						    		"""))) //

    // https://stackoverflow.com/questions/63836654/how-to-show-multiple-example-in-swagger-documentation
    @PostMapping(value = "/convert", consumes = { "multipart/form-data" })
    public ImageConverterResponse convert( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final MultipartFile file) {

	try {

	    final var kilobytes = (file.getSize() / 1024);

	    logger.info("begin with image {}, size {}", file.getName(), kilobytes);

	    final var result = imageConversionService.convert(new ImageConverterRequest(file, WS));

	    logger.info("end with result {}", result);

	    return result;

	} catch (final Throwable ex) {

	    logger.info("error: {}", getRootCauseMessage(ex));

	    throw ex;
	}
    }

    @Operation(summary = "Convert the image with specific area")
    @ApiResponse( //
		    responseCode = "200", //
		    description = "conversion susessful", //
		    content = { //
			    @Content(mediaType = "application/json", schema = @Schema(implementation = ImageConverterResponse.class)) //
		    } //
    )
    //
    @PostMapping(value = "/convertWithArea", consumes = { "multipart/form-data" })
    public ImageConverterResponse convertWithArea( //
		    @Parameter(description = "The Images to be uploaded", content = @Content(mediaType = "multipart/form-data"), required = true, schema = @Schema(allowableValues = "> 0")) //
		    @RequestParam(required = true) //
		    final MultipartFile file, //
		    //
		    @Parameter(description = "The vertical position", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final int x, //
		    //
		    @Parameter(description = "The horizontal position", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final int y, //
		    //
		    @Parameter(description = "The width size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final int width, //
		    //
		    @Parameter(description = "The height size", content = @Content(mediaType = "multipart/form-data"), required = true) //
		    @RequestParam(required = true) //
		    final int height) {

	try {

	    final var kilobytes = (file.getSize() / 1024);

	    logger.info("begin with image {}, size {}, x {}, y {}, width {}, height {}", file.getName(), kilobytes, x, y, width, height);

	    final var result = imageConversionService.convert(new ImageConverterRequest(file, WS, x, y, width, height));

	    logger.info("end with result {}", result);

	    return result;

	} catch (final Throwable ex) {

	    logger.info("error {}", getRootCauseMessage(ex));

	    throw ex;
	}
    }

    // http://localhost:8080/image-converter/rest/images/ping
    @GetMapping("/ping")
    @ResponseStatus(OK)
    public String ping() {

	logger.info("begin");

	final var result = "ping!";

	logger.info("end");

	throw new IllegalArgumentException("Error tests");

//	return result;
    }
}