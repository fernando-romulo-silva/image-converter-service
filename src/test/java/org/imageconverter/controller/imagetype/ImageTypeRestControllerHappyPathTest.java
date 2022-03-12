package org.imageconverter.controller.imagetype;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.TestConstants.FILTER_PARAM_ID;
import static org.imageconverter.TestConstants.ID_PARAM_VALUE;
import static org.imageconverter.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.StringUtils;
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
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test the {@link ImageTypeRestController} controller on happy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
@Sql(statements = TestConstants.SQL_DELETE_FROM_IMAGE_TYPE_WHERE_IMT_EXTENSION_BMP)
//
@Tag("acceptance")
@ExtendWith(SpringExtension.class)
@DisplayName("Test the image type controller, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageTypeRestControllerHappyPathTest extends ImageTypeRestControllerUnHappyPathBaseTest {

    private final CreateImageTypeRequest createImageTypeRequest;

    @Autowired
    ImageTypeRestControllerHappyPathTest(final ObjectMapper mapper, final MockMvc mvc) {
	super(mapper, mvc);
	this.createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");
    }

    @Test
    @Order(1)
    @DisplayName("get a image type by id")
    void findImageTypeByIdTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// already on db, due to the db-data-test.sql
	final var id = "1000";

	mvc.perform(get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath(FILTER_PARAM_ID).value(id)) //
			.andReturn() //
	;
    }

    @Test
    @Order(2)
    @DisplayName("get all image types")
    void findAllImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

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

	// get all, the db-data-test.sql has png and jpg image types
	mvc.perform(get(REST_URL) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].extension").value(containsInAnyOrder("png", "jpg", createImageTypeRequest.extension()))) //
			.andReturn() //
	;

    }

    @Test
    @Order(3)
    @DisplayName("get a image type by search")
    void findImageTypeByExtensionTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// already on db, due to the db-data-test.sql
	final var extension = "png";

	mvc.perform(get(REST_URL + "/search") //
			.accept(MediaType.APPLICATION_JSON) //
			.param("filter", "extension:'" + extension + "'") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].extension").value(containsInAnyOrder(extension))) //
			.andReturn() //
	;
	
    }

    @Test
    @Order(4)
    @DisplayName("Create a new image type")
    void createImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// create a new image type
	final var result = mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
			.andReturn();

	// what's id?
	final var id = StringUtils.substringBetween(result.getResponse().getContentAsString(), "'", "'");

	// check if it exists
	mvc.perform(get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath(FILTER_PARAM_ID).value(id)) //
	;
    }

    @Test
    @Order(5)
    @DisplayName("Update a image type")
    void updateImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// create a new image type
	final var createResult = mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
			.andReturn();

	// what's id?
	final var createdId = StringUtils.substringBetween(createResult.getResponse().getContentAsString(), "'", "'");

	// create a new values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	// update the image type
	mvc.perform(put(REST_URL + ID_PARAM_VALUE, createdId) //
			.content(asJsonString(newTypeRequest)) //
			.contentType(MediaType.APPLICATION_JSON) //
			.accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;

	// check if it updtated
	mvc.perform(get(REST_URL + ID_PARAM_VALUE, createdId) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath(FILTER_PARAM_ID).value(createdId)) //
			.andExpect(jsonPath("$.name").value(newTypeRequest.name())) //
			.andReturn() //
	;
    }

    @Test
    @Order(6)
    @DisplayName("Delete a new image type")
    void deleteImageTypeTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)
	// already on db, due to the db-data-test.sql
	final var id = "1000";

	// check if the image type
	mvc.perform(get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath(FILTER_PARAM_ID).value(id)) //
	;

	// delete the image type
	mvc.perform(delete(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;

	// check it again
	mvc.perform(get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andReturn() //
	;

    }

    String asJsonString(final Object object) {

	try {
	    return mapper.writeValueAsString(object);
	} catch (final JsonProcessingException e) {
	    throw new IllegalStateException(e);
	}
    }
}
