package org.imageconverter.controller.imageconverter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.TestConstants.ID_PARAM_VALUE;
import static org.imageconverter.infra.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test the {@link ImageConversionRestController} controller on unhappy path
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
@DisplayName("Test the image conversion, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageConvertRestControllerSearchUnhappyPathTest {

    // JSqlParser
    // @Value("classpath:db/db-data-test.sql")
    // private Resource dbDataTest;

    private final MockMvc mvc;

    ImageConvertRestControllerSearchUnhappyPathTest(@Autowired MockMvc mvc) throws IOException {
	super();
	this.mvc = mvc;
    }

    @Test
    @Order(1)
    @DisplayName("Try to get a image conversion that not exists")
    void tryToGetImageConversionByIdTest() throws Exception {// NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "1234";

	final var request = get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageConversion with id '" + id + "' not found"))) //
	;

    }

    @Test
    @Order(2)
    @DisplayName("Search a image conversion that not exists by search")
    void tryToGetImageConversionBySearchTest() throws Exception {// NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var fileName = "some_file.png";

	final var request = get(REST_URL) //
			.param("filter", "fileName:'" + fileName + "'") //
			.accept(MediaType.APPLICATION_JSON) //
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
			.andExpect(jsonPath("$.content").isEmpty())
	;

    }

    @Test
    @Order(3)
    @DisplayName("Search a image conversion by invalid field search")
    void tryToGetImageConversionByInvalidFieldSearchTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var fileName = "some_file.png";

	final var request = get(REST_URL) //
			.param("filter", "fileName:'" + fileName + "' and fieldNotExist:'blablabla'") //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf());

	mvc.perform(request) //
			//
			// when
			.andDo(print()) //
			//
			// then
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("Unable to locate Attribute with the the given name 'fieldNotExist' on ImageConversion"))) //
	;

    }
    
    @Test
    @Order(4)
    @DisplayName("Try to delete a image conversion that doesn't exist")
    void deleteImageConversionDoesNotExistTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	// given
	final var id = "12356";

	final var request = delete(REST_URL + ID_PARAM_VALUE, id) //
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
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageConversion with id '" + id + "' not found"))) //
	;
    }

}
