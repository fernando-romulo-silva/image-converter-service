package org.imageconverter.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the image type controller, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageTypeRestControllerUnHappyPathTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private CreateImageTypeRequest createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

    @Test
    @Order(1)
    @DisplayName("Search a image type that doesn't exist by id")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void getImageTypeByIdTest() throws Exception {

	final var id = "1234";

	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageType with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("Search a image type that not exists by search")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void getImageTypeBySearchTest() throws Exception {

	final var extension = "bmp";

	mvc.perform(get(REST_URL + "/search?filter=extension:'" + extension + "'") //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(content().string("[]")) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("Create twice the same image type")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void createSameImageTypeTest() throws Exception {

	// create one
	mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
	;

	// create another
	mvc.perform(post(REST_URL) //
			.content(asJsonString(createImageTypeRequest)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isConflict()) // ImageType with extension 'BMP' already exists
			.andExpect(jsonPath("$.message").value(containsString("ImageType with extension '" + createImageTypeRequest.extension() + "' already exists"))) //
	;
    }

    @Test
    @Order(4)
    @DisplayName("Try to create image type with invalid json")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void createInvalidImageTypeTest() throws Exception {

	// invalid json
	final var json = """
			{
			    "blabla": "BMP",
			    "name": "BitMap",
			    "description": "Device independent bitmap"
			}
								""";

	// try to create
	mvc.perform(post(REST_URL) //
			.content(json) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Missing required creator property 'extension'"))) //
	;
    }
    
    @Test
    @Order(5)
    @DisplayName("Try to create image type with invalid value")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void createInvalidImageTypeTest2() throws Exception {

	// invalid json
	final var json = """
                            {
                                "extension": "BMP",
                                "name": "",
                                "description": "Device independent bitmap"
                            } """;

	// try to create
	mvc.perform(post(REST_URL) //
			.content(json) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("The 'name' cannot be empty"))) //
	;
    }    

    @Test
    @Order(6)
    @DisplayName("Try to update a image type that doesn't exist")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void updateImageTypeDoesNotExistTest() throws Exception {

	// what's id?
	final var id = "12345";

	// update values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	// update the image type that doesn't exist
	mvc.perform(put(REST_URL + "/{id}", id) //
			.content(asJsonString(newTypeRequest)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageType with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(7)
    @DisplayName("Try to delete a image type that doesn't exist")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void deleteImageTypeTest() throws Exception {

	final var id = "12356";

	// delete the image type
	mvc.perform(delete(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageType with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(8)
    @DisplayName("Try to delete a image type that has a relation with other record")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void deleteImageTypeRestrictionTest() throws Exception {

	final var id = "1001";

	// delete the image type
	mvc.perform(delete(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isConflict()) //
			.andExpect(jsonPath("$.message").value(containsString("You cannot delete the image type 1001 because it is already used"))) //
	;
    }
    
    @Test
    @Order(9)
    @DisplayName("Try to access a invalid url")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void invalidUrlTest() throws Exception {

	mvc.perform(get(REST_URL + "/blablabla") //
			.accept(ALL, TEXT_HTML, APPLICATION_JSON, TEXT_PLAIN) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("Resource not found. Please check the /swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config for more information"))) //
	;

    }

    public String asJsonString(final Object object) {

	try {
	    return mapper.writeValueAsString(object);
	} catch (final JsonProcessingException e) {
	    throw new IllegalStateException(e);
	}
    }
}
