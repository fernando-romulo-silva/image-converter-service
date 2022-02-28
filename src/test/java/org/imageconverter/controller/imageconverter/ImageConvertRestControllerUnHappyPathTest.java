package org.imageconverter.controller.imageconverter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.TestConstants.ID_PARAM_VALUE;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

/**
 * Test the {@link ImageConverterRestController} controller on unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
//
@ExtendWith(SpringExtension.class)
@Tag("acceptance")
@DisplayName("Test the image convertion, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageConvertRestControllerUnHappyPathTest {

    // JSqlParser
    // @Value("classpath:db/db-data-test.sql")
    // private Resource dbDataTest;

    private final MockMvc mvc;

    ImageConvertRestControllerUnHappyPathTest( //
		    @Autowired //
		    final MockMvc mvc) throws IOException {
	super();
	this.mvc = mvc;
    }

    @Test
    @Order(1)
    @DisplayName("Try to get a image convertion that not exists")
    void tryToGetImageConvertionByIdTest() throws Exception {// NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	final var id = "1234";

	mvc.perform(get(REST_URL + ID_PARAM_VALUE, id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("ImageConvertion with id '" + id + "' not found"))) //
			.andReturn() //
	;

    }

    @Test
    @Order(2)
    @DisplayName("Search a image convertion that not exists by search")
    void tryToGetImageConvertionBySearchTest() throws Exception {// NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search") //
			.param("filter", "fileName:'" + fileName + "'") //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(content().string("[]")) //
			.andReturn() //
	;

    }

    @Test
    @Order(3)
    @DisplayName("Search a image convertion by invalid field search")
    void tryToGetImageConvertionByInvalidFieldSearchTest() throws Exception { // NOPMD - SignatureDeclareThrowsException (MockMvc throws Exception), JUnitTestsShouldIncludeAssert (MockMvc already do it)

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search") //
			.param("filter", "fileName:'" + fileName + "' and fieldNotExist:'blablabla'") //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath(TestConstants.JSON_MESSAGE).value(containsString("Unable to locate Attribute with the the given name 'fieldNotExist' on ImageConvertion"))) //
			.andReturn() //
	;

    }

}
