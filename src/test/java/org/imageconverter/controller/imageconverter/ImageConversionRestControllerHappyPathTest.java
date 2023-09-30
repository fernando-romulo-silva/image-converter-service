package org.imageconverter.controller.imageconverter;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.imageconverter.TestConstants.FILTER_PARAM_ID;
import static org.imageconverter.TestConstants.ID_PARAM_VALUE;
import static org.imageconverter.application.ImageConversionService.HEADER_FILE;
import static org.imageconverter.infra.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.apache.commons.lang3.math.NumberUtils;
import org.imageconverter.TestConstants;
import org.imageconverter.controller.ImageConversionRestController;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConversionPostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test the {@link ImageConversionRestController} controller on happy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("functional")
@DisplayName("Test the image conversion, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class ImageConversionRestControllerHappyPathTest {

    // JSqlParser
    // @Value("classpath:db/db-data-test.sql")
    // private Resource dbDataTest;

    public static final String HEADER_PARAMETER_LOCATION = "Location";

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    private final Resource bill01ImageFile;

    private final Resource bill02ImageFile;

    ImageConversionRestControllerHappyPathTest( //
		    @Autowired //
		    final ObjectMapper mapper, //
		    //
		    @Autowired //
		    final MockMvc mvc, //
		    //
		    @Value("classpath:bill01.png") //
		    final Resource bill01ImageFile,

		    @Value("classpath:bill02.png") //
		    final Resource bill02ImageFile) {
	super();
	this.mapper = mapper;
	this.mvc = mvc;
	this.bill01ImageFile = bill01ImageFile;
	this.bill02ImageFile = bill02ImageFile;
    }

    @Test
    @Order(1)
    @DisplayName("find a image conversion by id")
    void findImageConversionByIdTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1000"; // already on db, due to the db-data-test.sql

	final var request = get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
			.andExpect(jsonPath("$.text").value(TestConstants.DB_CONVERSION_NUMBER)) //
	;

    }

    @Test
    @Order(2)
    @DisplayName("find all image conversions")
    void findAllImageConversionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = get(REST_URL) //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	// get all, the db-data-test.sql
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$.content").isArray()) //
			.andExpect(jsonPath("$.content[*].text").value(containsInAnyOrder(TestConstants.DB_CONVERSION_NUMBER))) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("find a image conversion by search")
    void findImageTypeByExtensionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var fileName = "image_test.jpg"; // already on db, due to the db-data-test.sql

	final var request = get(REST_URL) //
			.param("filter", "fileName:'" + fileName + "'") //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$.content").isArray()) //
			.andExpect(jsonPath("$.content[*].file_name").value(containsInAnyOrder(fileName))) //
	;
    }

    @Test
    @Order(4)
    @DisplayName("convert one image")
    @Sql(statements = "DELETE FROM image_conversion ")
    void convertImageTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception)

	// given
	final var multipartFile = new MockMultipartFile("file", bill01ImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, bill01ImageFile.getInputStream());

	final var request = multipart(REST_URL) //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	// create one
	final var result = mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(header().string(HEADER_PARAMETER_LOCATION, notNullValue())) //
			.andReturn();

	final var response = mapper.readValue(result.getResponse().getContentAsString(), ImageConversionPostResponse.class);

	final var locationArray = result.getResponse().getHeader(HEADER_PARAMETER_LOCATION).split("/");
	final var id = Long.valueOf(locationArray[locationArray.length - 1]);

	// then
	assertThat(id) //
			.as("Check the response's id is greater than zero") //
			.isGreaterThan(NumberUtils.LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as("Check the number string") //
			.containsIgnoringCase(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);
    }

    @Test
    @Order(4)
    @DisplayName("convert more than one image")
    @Sql(statements = "DELETE FROM image_conversion ")
    void convertImagesTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception)

	// given
	final var multipartFile1 = new MockMultipartFile("files", bill01ImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, bill01ImageFile.getInputStream());
	final var multipartFile2 = new MockMultipartFile("files", bill02ImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, bill02ImageFile.getInputStream());

	final var request = multipart(REST_URL + "/multiple") //
			.file(multipartFile1) //
			.file(multipartFile2) //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	// create one
	final var resultMultiple = mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(header().string(HEADER_PARAMETER_LOCATION, notNullValue())) //
			.andReturn();

	final var response = mapper.readValue(resultMultiple.getResponse().getContentAsString(), ImageConversionPostResponse[].class);
	final var locationsArray = resultMultiple.getResponse().getHeader(HEADER_PARAMETER_LOCATION).split(";");

	for (final var location : locationsArray) {
	    final var locationArray = location.split("/");
	    final var id = Long.valueOf(locationArray[locationArray.length - 1]);

	    // then
	    assertThat(id) //
			    .as("Check the response's id is greater than zero") //
			    .isGreaterThan(NumberUtils.LONG_ZERO);
	}

	assertThat(response) //
			.as("Check the number string") //
			.anyMatch(responseElement -> containsIgnoreCase(deleteWhitespace(responseElement.text().replaceAll("[^x0-9]", "")), TestConstants.IMAGE_PNG_CONVERSION_NUMBER));
    }

    @Test
    @Order(5)
    @DisplayName("convert the image with area")
    @Sql(statements = "DELETE FROM image_conversion ")
    void convertAreaTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception)

	// given
	final var multipartFile = new MockMultipartFile("file", bill01ImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, bill01ImageFile.getInputStream());

	final var request = multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param(TestConstants.X_AXIS, TestConstants.X_AXIS_VALUE) //
			.param(TestConstants.Y_AXIS, TestConstants.Y_AXIS_VALUE) //
			.param(TestConstants.WIDTH, TestConstants.WIDTH_VALUE) //
			.param(TestConstants.HEIGHT, TestConstants.HEIGHT_VALUE) //
			.with(csrf());

	// create one
	final var resultArea = mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(header().string(HEADER_PARAMETER_LOCATION, notNullValue())) //
			.andReturn();

	final var response = mapper.readValue(resultArea.getResponse().getContentAsString(), ImageConversionPostResponse.class);

	final var locationArray = resultArea.getResponse().getHeader(HEADER_PARAMETER_LOCATION).split("/");
	final var id = Long.valueOf(locationArray[locationArray.length - 1]);

	// then
	assertThat(id) //
			.as("Check the response's are id is greater than zero") //
			.isGreaterThan(NumberUtils.LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as("Check the bar code value") //
			.isEqualTo(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);
    }

    @Test
    @Order(6)
    @DisplayName("Delete a new conversion")
    void deleteImageConversionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var multipartFile = new MockMultipartFile("file", bill01ImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, bill01ImageFile.getInputStream());

	final var requestCreate = multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param(TestConstants.X_AXIS, TestConstants.X_AXIS_VALUE) //
			.param(TestConstants.Y_AXIS, TestConstants.Y_AXIS_VALUE) //
			.param(TestConstants.WIDTH, TestConstants.WIDTH_VALUE) //
			.param(TestConstants.HEIGHT, TestConstants.HEIGHT_VALUE) //
			.with(csrf());

	// create one
	final var result = mvc.perform(requestCreate) //
			//
			// when
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(header().string(HEADER_PARAMETER_LOCATION, notNullValue())) //
			.andReturn();

	final var locationArray = result.getResponse().getHeader(HEADER_PARAMETER_LOCATION).split("/");
	final var id = Long.valueOf(locationArray[locationArray.length - 1]);

	final var request = get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(APPLICATION_JSON) //
			.with(csrf());

	// check if the conversion already exists
	mvc.perform(request) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath(FILTER_PARAM_ID).value(id)) //
	;

	// when
	// delete the conversion
	mvc.perform(delete(REST_URL + ID_PARAM_VALUE, id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;

	// Then
	// check it again
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
	;

    }
    
    @Test
    @Order(7)
    @DisplayName("Create a CSV file download test")
    void createCsvFileTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1000"; // already on db, due to the db-data-test.sql
	final var contentType = new MediaType("txt", "csv", Charset.forName("UTF-8"));

	final var headerString = String.join(";", HEADER_FILE);

	// when
	final var result = mvc.perform(get(REST_URL + "/export") //
			.accept(contentType, APPLICATION_JSON) // 
			.with(csrf())) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isOk()) //
			.andExpect(content().contentType(contentType))
			.andReturn();
	;
	
	// then
	final var content = result.getResponse().getContentAsString();
	
	// then
	assertThat(content)
		.as("Check if the file contains the header") //
		.contains(headerString) //
		.as("Check if the contains the conversion 1000")
		.contains(id);

    }
}
