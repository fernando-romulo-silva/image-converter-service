package org.imageconverter.controller.imagetype;

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
import org.imageconverter.controller.ImageTypeRestController;
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
class ImageTypeRestControllerUnHappyPathMovTest extends ImageTypeRestControllerUnHappyPathBaseTest {

    private final CreateImageTypeRequest createImageTypeRequest;

    @Autowired
    ImageTypeRestControllerUnHappyPathMovTest(final ObjectMapper mapper, final MockMvc mvc) {
	super(mapper, mvc);
	this.createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");
    }

    @Test
    @Order(4)
    @DisplayName("Create twice the same image type")
    void createSameImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	// create one
	final var content = asJsonString(createImageTypeRequest);

	final var request = post(REST_URL) //
			.content(content) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
	;

	// create another
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isConflict()) // ImageType with extension 'BMP' already exists
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with extension '" + createImageTypeRequest.extension() + "' already exists"))) //
	;

    }

    @Test
    @Order(5)
    @DisplayName("Try to create image type with invalid json")
    void createInvalidImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	// invalid json
	final var json = """
			{
			    "blabla": "BMP",
			    "name": "BitMap",
			    "description": "Device independent bitmap"
			}
								""";

	final var request = post(REST_URL) //
			.content(json) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf());

	// try to create
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("Missing required creator property 'extension'"))) //
	;
    }

    @Test
    @Order(6)
    @DisplayName("Try to create image type with invalid value")
    void createInvalidImageTypeTest2() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	// invalid json
	final var json = """
			{
			    "extension": "BMP",
			    "name": "",
			    "description": "Device independent bitmap"
			} """;

	final var request = post(REST_URL) //
			.content(json) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf());

	// try to create
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("The 'name' cannot be empty"))) //
	;

    }

    @Test
    @Order(7)
    @DisplayName("Try to update a image type that doesn't exist")
    void updateImageTypeDoesNotExistTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "12345";

	// update values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	final var request = put(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.content(asJsonString(newTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf());

	// update the image type that doesn't exist
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(8)
    @DisplayName("Try to delete a image type that doesn't exist")
    void deleteImageTypeDoesNotExistTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "12356";

	final var request = delete(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	// delete the image type
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageType with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(9)
    @DisplayName("Try to delete a image type that has a relation with other record")
    void deleteImageTypeRestrictionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1001";

	final var request = delete(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	// delete the image type
	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isConflict()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("You cannot delete the image type '1001' because it is already used"))) //
	;
    }

    @Test
    @Order(10)
    @DisplayName("Try to access a invalid url")
    void invalidUrlTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = get(REST_URL + "/blablabla") //
			.accept(MediaType.ALL, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE)
					.value(containsString("Resource not found. Please check the /swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config for more information"))) //
	;

    }
}
