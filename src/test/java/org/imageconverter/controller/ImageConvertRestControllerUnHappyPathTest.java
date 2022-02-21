package org.imageconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.TestConstants.HEIGHT;
import static org.imageconverter.TestConstants.HEIGHT_VALUE;
import static org.imageconverter.TestConstants.WIDTH;
import static org.imageconverter.TestConstants.WIDTH_VALUE;
import static org.imageconverter.TestConstants.X_AXIS;
import static org.imageconverter.TestConstants.X_AXIS_VALUE;
import static org.imageconverter.TestConstants.Y_AXIS;
import static org.imageconverter.TestConstants.Y_AXIS_VALUE;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
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
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@ExtendWith(SpringExtension.class)
@Tag("acceptance")
@DisplayName("Test the image convertion, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class ImageConvertRestControllerUnHappyPathTest {

    // JSqlParser
    // @Value("classpath:db/db-data-test.sql")
    // private Resource dbDataTest;

    @Value("classpath:beach.jpeg")
    private Resource beachImageFile;

    @Value("classpath:bill.png")
    private Resource billImageFile;

    @Value("classpath:corruptedImage.png")
    private Resource corruptedImageFile;

    @Value("classpath:emptyImage.png")
    private Resource emptyImageFile;

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    @DisplayName("Try to get a image convertion that not exists")
    void tryToGetImageConvertionByIdTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var id = "1234";

	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageConvertion with id '" + id + "' not found"))); //
    }

    @Test
    @Order(2)
    @DisplayName("Search a image convertion that not exists by search")
    void tryToGetImageConvertionBySearchTest() throws Exception {// NOPMD - MockMvc throws Exception

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search") //
			.param("filter", "fileName:'" + fileName + "'") //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(content().string("[]")); //
    }

    @Test
    @Order(3)
    @DisplayName("Search a image convertion by invalid field search")
    void tryToGetImageConvertionByInvalidFieldSearchTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search") //
			.param("filter", "fileName:'" + fileName + "' and fieldNotExist:'blablabla'") //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Unable to locate Attribute with the the given name 'fieldNotExist' on ImageConvertion"))) //
			.andReturn();
    }

    @Test
    @Order(4)
    @DisplayName("convert the image with unknow extension")
    void tryToConvertTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

	mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageType with extension jpeg not found"))) //
			.andReturn();
    }

    @Test
    @Order(5)
    @DisplayName("Convert the same image")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    void tryToConvertSameImageTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	final var multipartFile = new MockMultipartFile("file", billImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, billImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andReturn();

	// create another
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isConflict()) //
			.andExpect(jsonPath("$.message").value(containsString("ElementAlreadyExistsException: ImageConvertion with fileName 'bill.png'"))) //
	;
    }

    @Test
    @Order(6)
    @DisplayName("Convert a corrupted image")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    void tryToConvertCorruptedImageTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	final var multipartFile = new MockMultipartFile("file", corruptedImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, corruptedImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(7)
    @DisplayName("Convert a empty image")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    void tryToConvertEmptyImageTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	final var multipartFile = new MockMultipartFile("file", emptyImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, emptyImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isServiceUnavailable()) //
			.andExpect(jsonPath("$.message").value(containsString("Image emptyImage.png has Tessarct error: 'IllegalArgumentException: image == null!'"))) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());

    }

    @Test
    @Order(8)
    @DisplayName("Convert a corrupted image with area")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    void tryToConvertCorruptedImageAreaTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	final var multipartFile = new MockMultipartFile("file", corruptedImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, corruptedImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
			.andReturn();

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(9)
    @DisplayName("Convert a empty image with area")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    void tryToConvertEmptyImageAreaTest() throws Exception { // NOPMD - MockMvc throws Exception

	// create one
	final var multipartFile = new MockMultipartFile("file", emptyImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, emptyImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isServiceUnavailable()) //
			.andReturn() //
	;

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());

    }

    @Test
    @Order(10)
    @DisplayName("convert the image with area with parameter null")
    void tryToConvertAreaParameterNullTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, Y_AXIS_VALUE) //
			.param(WIDTH, WIDTH_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("MissingServletRequestParameterException: The parameter 'height' is missing"))) //
			.andReturn();

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @Order(11)
    @DisplayName("convert the image with area with parameter Y invalid")
    void tryToConvertAreaParameterInvalidYTest() throws Exception { // NOPMD - MockMvc throws Exception

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(MediaType.APPLICATION_JSON) //
			.param(X_AXIS, X_AXIS_VALUE) //
			.param(Y_AXIS, "-1") //
			.param(WIDTH, WIDTH_VALUE) //
			.param(HEIGHT, HEIGHT_VALUE) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("The y point must be greater than zero"))) //
			.andReturn();

	assertThat(result.getResponse().getStatus()) //
			.isEqualTo(HttpStatus.BAD_REQUEST.value());

    }
}
