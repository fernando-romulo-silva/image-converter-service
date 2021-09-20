package org.imageconverter.controller;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.util.controllers.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.imageconverter.util.controllers.CreateImageTypeRequest;
import org.imageconverter.util.controllers.UpdateImageTypeRequest;
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
    @DisplayName("get a image type by id that not exists")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void getImageTypeByIdTest() throws Exception {

	// already on db, due to the db-data-test.sql
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
    @DisplayName("get a image type by search that not exists")
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
    @DisplayName("create same image type")
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
    @Order(3)
    @DisplayName("create same image type")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void createInvalidImageTypeTest() throws Exception {

	final var json = """
				{
					"text": "some text",
					"id" : 10
				}	
					""";
	
	// create another
	mvc.perform(post(REST_URL) //
			.content(json) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) // ImageType with extension 'BMP' already exists
			.andExpect(jsonPath("$.message").value(containsString("ImageType with extension '" + createImageTypeRequest.extension() + "' already exists"))) //
	;
    }    

    @Test
    @Order(5)
    @DisplayName("Update a image type doesn't exists")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void updateImageTypeDoesNotExistTest() throws Exception {

	// what's id?
	final var id = "12345";

	// create a new values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	// update the image type
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

    public String asJsonString(final Object object) {

	try {
	    return mapper.writeValueAsString(object);
	} catch (final JsonProcessingException e) {
	    throw new IllegalStateException(e);
	}
    }
}
