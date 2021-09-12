package org.imageconverter.controller;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.util.controllers.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
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
//
@Tag("acceptance")
@DisplayName("Test the image type controller, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageTypeRestControllerHappyPathTest {

    @Autowired
    private ObjectMapper mapper;

    // JSqlParser
    @Value("classpath:db/db-data-test.sql")
    private Resource dbDataTest;

    @Value("classpath:image.png")
    private Resource imageFile;

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    @DisplayName("get a image type by id")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
    public void getImageTypeByIdTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var id = "1000";

	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("get all image types")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
    public void getAllImageTypeTest() throws Exception {

	final var type = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

	// create one
	mvc.perform(post(REST_URL) //
			.content(asJsonString(type)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
	;

	// get all, the db-data-test.sql has png and jpg image types
	mvc.perform(get(REST_URL) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].extension").value(containsInAnyOrder("png", "jpg", type.extension()))) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("get a image type by search")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
    public void getImageTypeByExtensionTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var extension = "png";

	mvc.perform(get(REST_URL + "/search?filter=extension:'" + extension + "'") //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].extension").value(containsInAnyOrder(extension)));
    }

    @Test
    @Order(4)
    @DisplayName("Create a new image type")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql")
    public void createImageTypeTest() throws Exception {

	final var type = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

	// create a new image type
	final var result = mvc.perform(post(REST_URL) //
			.content(asJsonString(type)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
			.andReturn();

	// what's id?
	final var id = substringBetween(result.getResponse().getContentAsString(), "'", "'");

	// check if it exists
	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
	;

    }

    @Test
    @Order(5)
    @DisplayName("Update a image type")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql")
    public void updateImageTypeTest() throws Exception {

	final var typeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

	// create a new image type
	final var result = mvc.perform(post(REST_URL) //
			.content(asJsonString(typeRequest)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andExpect(content().string(containsString("created"))) //
			.andReturn();

	// what's id?
	final var id = substringBetween(result.getResponse().getContentAsString(), "'", "'");

	// check if it exists
	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
			.andExpect(jsonPath("$.name").value(typeRequest.name())) //
	;

	// create a new values
	final var newTypeRequest = new UpdateImageTypeRequest(null, "Bitmap", null);

	// update the image type
	mvc.perform(put(REST_URL + "/{id}", id) //
			.content(asJsonString(newTypeRequest)) //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;

	// check if it updtated
	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
			.andExpect(jsonPath("$.name").value(newTypeRequest.name())) //
	;
    }

    @Test
    @Order(6)
    @DisplayName("Delete a new image type")
    @Sql(statements = "DELETE FROM image_type")
    @Sql(scripts = "classpath:db/db-data-test.sql")
    public void deleteImageTypeTest() throws Exception {
	// already on db, due to the db-data-test.sql
	final var id = "1000";

	// check if the image type
	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(id)) //
	;

	// delte the image type
	mvc.perform(delete(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNoContent()) //
	;

	// check it again
	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
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
