package org.imageconverter.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
public class ImageConvertRestControllerUnHappyPathTest {

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
    public void tryToGetImageConvertionByIdTest() throws Exception {

	final var id = "1234";

	mvc.perform(get(REST_URL + "/{id}", id) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isNotFound()) //
			.andExpect(jsonPath("$.message").value(containsString("ImageConvertion with id '" + id + "' not found"))); //
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
			.andExpect(content().string("[]")); //
    }

    @Test
    @Order(3)
    @DisplayName("Search a image convertion by invalid field search")
    public void getImageConvertionByInvalidFieldSearchTest() throws Exception {

	final var fileName = "some_file.png";

	mvc.perform(get(REST_URL + "/search?filter=fileName:'" + fileName + "' and filedNotExist:'blablabla'") //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Unable to locate Attribute with the the given name 'filedNotExist' on ImageConvertion"))) //
			.andReturn();
    }

    @Test
    @Order(4)
    @DisplayName("convert the image with unknow extension")
    public void convertTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

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
    @DisplayName("Convert the same image")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void convertSameImageTest() throws Exception {

	// create one
	final var multipartFile = new MockMultipartFile("file", billImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, billImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "1417") //
			.param("width", "1426") //
			.param("height", "57") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andReturn();
	;

	// create another
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "1417") //
			.param("width", "1426") //
			.param("height", "57") //
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
    public void convertCorruptedImageTest() throws Exception {

	// create one
	final var multipartFile = new MockMultipartFile("file", corruptedImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, corruptedImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
	;

    }
    
    
    @Test
    @Order(7)
    @DisplayName("Convert a empty image")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void convertEmptyImageTest() throws Exception {

	// create one
	final var multipartFile = new MockMultipartFile("file", emptyImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, emptyImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isServiceUnavailable()) //
			.andExpect(jsonPath("$.message").value(containsString("Image emptyImage.png has Tessarct error: 'IllegalArgumentException: image == null!'"))) //
	;

    } 
    
    @Test
    @Order(8)
    @DisplayName("Convert a corrupted image with area")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void convertCorruptedImageAreaTest() throws Exception {

	// create one
	final var multipartFile = new MockMultipartFile("file", corruptedImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, corruptedImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "1417") //
			.param("width", "1426") //
			.param("height", "57") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isBadRequest()) //
			.andExpect(jsonPath("$.message").value(containsString("Image corruptedImage.png has IO error: 'IIOException: Image width <= 0!'"))) //
	;

    }
    
    
    @Test
    @Order(9)
    @DisplayName("Convert a empty image with area")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void convertEmptyImageAreaTest() throws Exception {

	// create one
	final var multipartFile = new MockMultipartFile("file", emptyImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, emptyImageFile.getInputStream());

	// create one
	mvc.perform(multipart(REST_URL + "/area") //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.param("x", "885") //
			.param("y", "1417") //
			.param("width", "1426") //
			.param("height", "57") //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isServiceUnavailable()) //
			.andExpect(jsonPath("$.message").value(containsString("Image emptyImage.png has Tessarct error: 'IllegalArgumentException: image == null!'"))) //
	;

    }    

    @Test
    @Order(10)
    @DisplayName("convert the image with area with parameter null")
    public void convertAreaParameterNullTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

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
    @Order(11)
    @DisplayName("convert the image with area with parameter Y invalid")
    public void convertAreaParameterInvalidYTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", beachImageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, beachImageFile.getInputStream());

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
