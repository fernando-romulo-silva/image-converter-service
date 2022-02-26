package org.imageconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.imageconverter.TestConstants;
import org.imageconverter.util.controllers.imagetype.CreateImageTypeRequest;
import org.imageconverter.util.controllers.imagetype.UpdateImageTypeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test the {@link ImageTypeRestController} controller on unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
@Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
//
@Tag("acceptance")
@ExtendWith(SpringExtension.class)
@DisplayName("Test the image type controller, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class ImageTypeRestControllerUnHappyPathTest extends ImageTypeRestControllerUnHappyPathBaseTest {

    private final CreateImageTypeRequest createImageTypeRequest;
    
    @Autowired
    ImageTypeRestControllerUnHappyPathTest(final ObjectMapper mapper, final MockMvc mvc) {
	super(mapper, mvc);
	this.createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");   
    }

    @Test
    @Order(4)
    @DisplayName("Create twice the same image type")
    void createSameImageTypeTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
	;

	// create another
	final var result = mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isConflict()) // ImageType with extension 'BMP' already exists
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with extension '" + createImageTypeRequest.extension() + "' already exists"))) //
			.andReturn();

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.CONFLICT.value());

    }

    @Test
    @Order(5)
    @DisplayName("Try to create image type with invalid json")
    void createInvalidImageTypeTest() throws Exception { // NOPMD - MockMvc throws Exception

	// invalid json
	final var json = """
			{
			    "blabla": "BMP",
			    "name": "BitMap",
			    "description": "Device independent bitmap"
			}
								""";

	// try to create
	final var result = mvc.perform(post(REST_URL) //
			.content(json) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("Missing required creator property 'extension'"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(6)
    @DisplayName("Try to create image type with invalid value")
    void createInvalidImageTypeTest2() throws Exception { // NOPMD - MockMvc throws Exception

	// invalid json
	final var json = """
			{
			    "extension": "BMP",
			    "name": "",
			    "description": "Device independent bitmap"
			} """;

	// try to create
	final var result = mvc.perform(post(REST_URL) //
			.content(json) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("The 'name' cannot be empty"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(7)
    @DisplayName("Try to update a image type that doesn't exist")
    void updateImageTypeDoesNotExistTest() throws Exception { // NOPMD - MockMvc throws Exception

	// what's id?
	final var id = "12345";

	// update values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	// update the image type that doesn't exist
	final var result = mvc.perform(put(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.content(asJsonString(newTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with id '" + id + "' not found"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(8)
    @DisplayName("Try to delete a image type that doesn't exist")
    void deleteImageTypeDoesNotExistTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var id = "12356";

	// delete the image type
	final var result = mvc.perform(delete(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with id '" + id + "' not found"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(9)
    @DisplayName("Try to delete a image type that has a relation with other record")
    void deleteImageTypeRestrictionTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var id = "1001";

	// delete the image type
	final var result = mvc.perform(delete(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isConflict()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("You cannot delete the image type '1001' because it is already used"))) //
			.andReturn() //
	;
	
	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(10)
    @DisplayName("Try to access a invalid url")
    void invalidUrlTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var result = mvc.perform(get(REST_URL + "/blablabla") //
			.accept(MediaType.ALL, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE)
					.value(containsString("Resource not found. Please check the /swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config for more information"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.NOT_FOUND.value());

    }
}
