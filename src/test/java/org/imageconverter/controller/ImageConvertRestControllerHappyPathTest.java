package org.imageconverter.controller;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.math.NumberUtils.LONG_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.util.controllers.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.imageconverter.util.controllers.ImageConverterResponse;
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
//
@Tag("acceptance")
@DisplayName("Test the image convertion, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageConvertRestControllerHappyPathTest {

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
    @Order(2)
    @DisplayName("convert the image")
    @Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
    public void convertTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL) //
			.file(multipartFile) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
			.andReturn();

	final var response = mapper.readValue(result.getResponse().getContentAsString(), ImageConverterResponse.class);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);
    }

    @Test
    @Order(2)
    @DisplayName("convert the image with area")
    @Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
    public void convertAreaTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	// create one
	final var result = mvc.perform(multipart(REST_URL + "/area") //
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

	final var response = mapper.readValue(result.getResponse().getContentAsString(), ImageConverterResponse.class);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);

	assertThat("03399905748110000007433957701015176230000017040") //
			.isEqualTo(deleteWhitespace(response.text()).replaceAll("[^x0-9]", ""));
    }
}
