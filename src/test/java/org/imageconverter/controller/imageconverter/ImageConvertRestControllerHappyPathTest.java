package org.imageconverter.controller.imageconverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.imageconverter.TestConstants;
import org.imageconverter.controller.ImageConverterRestController;
import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test the {@link ImageConverterRestController} controller on happy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the image convertion, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class ImageConvertRestControllerHappyPathTest {

    // JSqlParser
    // @Value("classpath:db/db-data-test.sql")
    // private Resource dbDataTest;

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    private final Resource billImageFile;

    ImageConvertRestControllerHappyPathTest(@Autowired final ObjectMapper mapper, @Autowired final MockMvc mvc, @Value("classpath:bill.png") final Resource billImageFile) {
	super();
	this.mapper = mapper;
	this.mvc = mvc;
	this.billImageFile = billImageFile;
    }

    @Test
    @Order(1)
    @DisplayName("find a image convertion by id")
    void findImageConvertionByIdTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

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
			.andExpect(jsonPath("$.text").value(TestConstants.DB_CONVERTION_NUMBER)) //
	;

    }

    @Test
    @Order(2)
    @DisplayName("find all image convertions")
    void findAllImageConvertionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

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
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].text").value(containsInAnyOrder(TestConstants.DB_CONVERTION_NUMBER))) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("find a image convertion by search")
    void findImageTypeByExtensionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var fileName = "image_test.jpg"; // already on db, due to the db-data-test.sql

	final var request = get(REST_URL + "/search") //
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
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].file_name").value(containsInAnyOrder(fileName))) //
	;
    }

    @Test
    @Order(4)
    @DisplayName("convert the image")
    @Sql(statements = "DELETE FROM image_convertion ")
    void convertTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception)

	// given
	final var multipartFile = new MockMultipartFile("file", billImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, billImageFile.getInputStream());

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
			.andReturn();

	final var response = mapper.readValue(result.getResponse().getContentAsString(), ImageConverterResponse.class);

	// then
	assertThat(response.id()) //
			.as("Check the response's id is greater than zero") //
			.isGreaterThan(NumberUtils.LONG_ZERO);

	assertThat(StringUtils.deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as("Check the number string") //
			.containsIgnoringCase(TestConstants.IMAGE_PNG_CONVERTION_NUMBER);
    }

    @Test
    @Order(5)
    @DisplayName("convert the image with area")
    @Sql(statements = "DELETE FROM image_convertion ")
    void convertAreaTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception)

	// given
	final var multipartFile = new MockMultipartFile("file", billImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, billImageFile.getInputStream());

	final var request = multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param(TestConstants.X_AXIS, TestConstants.X_AXIS_VALUE) //
			.param(TestConstants.Y_AXIS, TestConstants.Y_AXIS_VALUE) //
			.param(TestConstants.WIDTH, TestConstants.WIDTH_VALUE) //
			.param(TestConstants.HEIGHT, TestConstants.HEIGHT_VALUE) //
			.with(csrf());
	
	// create one
	final var result = mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andReturn();

	final var response = mapper.readValue(result.getResponse().getContentAsString(), ImageConverterResponse.class);

	// then
	assertThat(response.id()) //
			.as("Check the response's id is greater than zero") //
			.isGreaterThan(NumberUtils.LONG_ZERO);

	assertThat(StringUtils.deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as("Check the number string") //
			.isEqualTo(TestConstants.IMAGE_PNG_CONVERTION_NUMBER);
    }
}
