package org.imageconverter.controller.imageconverter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.TestConstants.HEIGHT;
import static org.imageconverter.TestConstants.HEIGHT_VALUE;
import static org.imageconverter.TestConstants.JSON_MESSAGE;
import static org.imageconverter.TestConstants.WIDTH;
import static org.imageconverter.TestConstants.WIDTH_VALUE;
import static org.imageconverter.TestConstants.X_AXIS;
import static org.imageconverter.TestConstants.X_AXIS_VALUE;
import static org.imageconverter.TestConstants.Y_AXIS;
import static org.imageconverter.TestConstants.Y_AXIS_VALUE;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL_AREA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.imageconverter.TestConstants;
import org.imageconverter.controller.ImageConverterRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test the {@link ImageConverterRestController} controller on convert unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the image convertion, unhappy convert path :( ????  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageConvertRestControllerConvertUnHappyPathTest {

    private final MockMvc mvc;

    private final MockMultipartFile multipartBeachImageFile;

    private final MockMultipartFile multipartBillImageFile;

    private final MockMultipartFile multipartEmptyImageFile;

    private final MockMultipartFile multipartCorruptedImageFile;

    ImageConvertRestControllerConvertUnHappyPathTest( //
		    @Value("classpath:beach.jpeg") //
		    final Resource beachImageFile, //
		    //
		    @Value("classpath:bill.png") //
		    final Resource billImageFile, //
		    //
		    @Value("classpath:corruptedImage.png") //
		    final Resource corruptedImageFile, //
		    //
		    @Value("classpath:emptyImage.png") //
		    final Resource emptyImageFile, //
		    //
		    @Autowired //
		    final MockMvc mvc) throws IOException {
	super();
	this.mvc = mvc;

	final var fileString = "file";

	this.multipartBillImageFile = new MockMultipartFile(fileString, billImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, billImageFile.getInputStream());
	this.multipartBeachImageFile = new MockMultipartFile(fileString, beachImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());
	this.multipartEmptyImageFile = new MockMultipartFile(fileString, emptyImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, emptyImageFile.getInputStream());
	this.multipartCorruptedImageFile = new MockMultipartFile(fileString, corruptedImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, corruptedImageFile.getInputStream());
    }

    @Test
    @Order(1)
    @DisplayName("convert the image with unknow extension")
    void tryToConvertTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL) //
			.file(multipartBeachImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("ImageType with extension jpeg not found"))) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("Convert the same image")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertSameImageTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartBillImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf());

	// create one
	mvc.perform(request) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
	;

	// create another
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isConflict()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("ElementAlreadyExistsException: ImageConvertion with fileName 'bill.png'"))) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("Convert a corrupted image")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertCorruptedImageTest() throws Exception {// NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL) //
			.file(multipartCorruptedImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	// create one
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
	;

    }

    @Test
    @Order(4)
    @DisplayName("Convert a empty image")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertEmptyImageTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL) //
			.file(multipartEmptyImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	// create one
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isServiceUnavailable()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Image emptyImage.png has Tessarct error: 'IllegalArgumentException: image == null!'"))) //
	;
    }

    @Test
    @Order(5)
    @DisplayName("Convert a corrupted image with area")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertCorruptedImageAreaTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartCorruptedImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
	;
    }

    @Test
    @Order(6)
    @DisplayName("Convert a empty image with area")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertEmptyImageAreaTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartEmptyImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isServiceUnavailable()) //
	;

    }

    @Test
    @Order(7)
    @DisplayName("convert the image with area with parameter null")
    void tryToConvertAreaParameterNullTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartBeachImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("MissingServletRequestParameterException: The parameter 'height' is missing"))) //
	;

    }

    @Test
    @Order(8)
    @DisplayName("convert the image with area with parameter Y invalid")
    void tryToConvertAreaParameterInvalidYTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartBeachImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, "-1") //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("The y point must be greater than zero"))) //
	;
    }

}
