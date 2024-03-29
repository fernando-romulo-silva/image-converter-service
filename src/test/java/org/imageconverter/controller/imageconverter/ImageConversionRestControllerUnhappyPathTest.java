package org.imageconverter.controller.imageconverter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.TestConstants.HEIGHT;
import static org.imageconverter.TestConstants.HEIGHT_VALUE;
import static org.imageconverter.TestConstants.ID_PARAM_VALUE;
import static org.imageconverter.TestConstants.JSON_MESSAGE;
import static org.imageconverter.TestConstants.WIDTH;
import static org.imageconverter.TestConstants.WIDTH_VALUE;
import static org.imageconverter.TestConstants.X_AXIS;
import static org.imageconverter.TestConstants.X_AXIS_VALUE;
import static org.imageconverter.TestConstants.Y_AXIS;
import static org.imageconverter.TestConstants.Y_AXIS_VALUE;
import static org.imageconverter.infra.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.imageconverter.infra.util.controllers.imageconverter.ImageConverterConst.REST_URL_AREA;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import org.imageconverter.TestConstants;
import org.imageconverter.controller.ImageConversionRestController;
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
 * Test the {@link ImageConversionRestController} controller on convert unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
//
@Tag("functional")
@DisplayName("Test the image conversion, unhappy convert path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageConversionRestControllerUnhappyPathTest {

    private static final String VALUE_TEXT = "value";

    private static final String ERROR_TEXT = "error";

    private static final String FIELD_TEXT = "field";

    private final MockMvc mvc;

    private final MockMultipartFile multipartBeachImageFile;

    private final MockMultipartFile multipartBillImageFile;

    private final MockMultipartFile multipartEmptyImageFile;

    private final MockMultipartFile multipartCorruptedImageFile;

    ImageConversionRestControllerUnhappyPathTest( //
		    @Value("classpath:images/beach.jpeg") //
		    final Resource beachImageFile, //
		    //
		    @Value("classpath:images/bill01.png") //
		    final Resource billImageFile, //
		    //
		    @Value("classpath:images/corruptedImage.png") //
		    final Resource corruptedImageFile, //
		    //
		    @Value("classpath:images/emptyImage.png") //
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
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("ElementAlreadyExistsException: The ImageConversion with field(s) fileName 'bill01.png' already exists"))) //
			                                                        
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
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Image emptyImage.png has IO error: 'IllegalArgumentException: image == null!'"))) //
			                                                        
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
    @Order(5)
    @DisplayName("Convert a corrupted image with area, em portuguese")
    @Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
    void tryToConvertCorruptedImageAreaTestPortuguese() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartCorruptedImageFile) //
			.locale(new Locale("pt", "BR"))
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
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("A imagem corruptedImage.png tem erro de E/S: 'IIOException: Image width <= 0!'"))) //
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
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("The parameter 'height' is missing"))) //
	;

    }

    @Test
    @Order(8)
    @DisplayName("convert the image with area with parameter Y invalid")
    void tryToConvertAreaParameterInvalidYTest() throws Exception { // NOPMD - SignatureDeclareThrowsException: (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartBeachImageFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, "-1") //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, "-1") //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Constraint violation"))) //
			.andExpect(jsonPath("$.subErrors", containsInAnyOrder( //
					Map.of(FIELD_TEXT, "height", ERROR_TEXT, "The 'height' cannot be less than zero", VALUE_TEXT, "-1"), //
					Map.of(FIELD_TEXT, "yAxis", ERROR_TEXT, "The axis 'y' cannot be less than zero", VALUE_TEXT, "-1"))) //
			)			
	;
    }
    
    @Test
    @Order(9)
    @DisplayName("convert the image with area with parameter Y invalid")
    void tryToConvertAreaParameterInvalidYTestPortuguese() throws Exception { // NOPMD - SignatureDeclareThrowsException: (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = multipart(REST_URL_AREA) //
			.file(multipartBeachImageFile) //
			.locale(new Locale("pt", "BR"))
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, "-1") //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, "-1") //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("Violação de restrição"))) //
			.andExpect(jsonPath("$.subErrors", containsInAnyOrder( //
					Map.of(FIELD_TEXT, "height", ERROR_TEXT, "A 'altura' não pode ser menor que zero", VALUE_TEXT, "-1"), //
					Map.of(FIELD_TEXT, "yAxis", ERROR_TEXT, "O eixo 'y' não pode ser menor que zero", VALUE_TEXT, "-1"))) //
			)			
	;
    }
    
    @Test
    @Order(10)
    @DisplayName("Create an empty CSV file download test")
    void createEmptyCsvFileTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1000"; // already on db, due to the db-data-test.sql
	
	mvc.perform(delete(REST_URL + ID_PARAM_VALUE, id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;	
	
	final var contentType = new MediaType("txt", "csv", Charset.forName("UTF-8"));

	// when
	mvc.perform(get(REST_URL + "/export") //
			.accept(contentType, APPLICATION_JSON) // 
			.with(csrf())) //
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(JSON_MESSAGE).value(containsString("CsvFileNoDataException: No results to filter null"))) //
			.andReturn();
	;

    }

}
