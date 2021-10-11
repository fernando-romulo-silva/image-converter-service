package org.imageconverter.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
@DisplayName("Test the image convertion, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageConvertRestControllerUnHappyPathTest {

    @Autowired
    private ObjectMapper mapper;

    // JSqlParser
    @Value("classpath:db/db-data-test.sql")
    private Resource dbDataTest;

    @Value("classpath:beach.jpeg")
    private Resource imageFile;

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    @DisplayName("Try to get a image convertion that not exists")
    public void tryToGetImageConvertionByIdTest() throws Exception {

	final var id = "1234";

	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageConvertion with id '" + id + "' not found"))) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("Search a image convertion that not exists by search")
    public void getImageConvertionBySearchTest() throws Exception {

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search?filter=fileName:'" + fileName + "'") //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(content().string("[]")) //
	;
    }

    @Test
    @Order(3)
    @DisplayName("convert the image with unknow extension")
    public void convertTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageType with extension jpeg not found"))) //
			.andReturn();
    }

    @Test
    @Order(5)
    @DisplayName("convert the image with area with parameter null")
    public void convertAreaParameterNullTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "1417") //
			.param("width", "1426") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("MissingServletRequestParameterException: The parameter 'height' is missing"))) //
			.andReturn();

    }

    @Test
    @Order(6)
    @DisplayName("convert the image with area with parameter Y invalid")
    public void convertAreaParameterInvalidYTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "-1") //
			.param("width", "1426") //
			.param("height", "57") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("The y point must be greater than zero"))) //
			.andReturn();

    }
}
