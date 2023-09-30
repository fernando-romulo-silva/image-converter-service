package org.imageconverter.controller.imagetype;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.infra.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.imageconverter.TestConstants;
import org.imageconverter.controller.ImageTypeRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
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
@Tag("functional")
@DisplayName("Test the image type controller, find methods, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class ImageTypeRestControllerUnHappyPathFindTest extends ImageTypeRestControllerUnHappyPathBaseTest {

    @Autowired
    ImageTypeRestControllerUnHappyPathFindTest(final ObjectMapper mapper, final MockMvc mvc) {
	super(mapper, mvc);
    }

    @Test
    @Order(1)
    @DisplayName("Search a image type that doesn't exist by id")
    void findImageTypeByIdTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1234";

	final var request = get(REST_URL + TestConstants.ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request)
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
    @Order(2)
    @DisplayName("Search a image type that not exists by filter")
    void findImageTypeBySearchTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = get(REST_URL) //
			.accept(MediaType.APPLICATION_JSON) //
			.param("filter", "extension:'bmp'") //
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
			.andExpect(jsonPath("$.content").isEmpty()) //
	;

    }

    @Test
    @Order(3)
    @DisplayName("Search a image type by invalid filter")
    void findImageTypeBySearchInvalidSearchTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var request = get(REST_URL) //
			.accept(MediaType.APPLICATION_JSON) //
			.param("filter", "invalidField:'bmp'") //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("Unable to locate Attribute with the the given name 'invalidField' on ImageType"))) //
	;

    }
}
