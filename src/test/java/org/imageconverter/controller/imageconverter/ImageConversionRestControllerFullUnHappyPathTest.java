package org.imageconverter.controller.imageconverter;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.TestConstants.HTTP_127_0_0_1;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.springframework.http.ContentDisposition.builder;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.io.IOException;
import java.util.Objects;

import org.imageconverter.controller.actuator.BaseTesseractHealthTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;

/**
 * Test the actuator on unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the image conversion, unhappy path FULL :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ImageConversionRestControllerFullUnHappyPathTest extends BaseTesseractHealthTest {

    @Value("classpath:bill.png")
    private Resource imageFile;

//    @Disabled("csr is not working")
    @Test
    @Order(1)
    @DisplayName("convert the image with invalid tesseract conf")
    void convertWithInvalidTesseractConfTest() throws IOException {

	final var restTemplate = new RestTemplate();

	// given
	// ---------------------------------------------------------------------------------------------------------------
	final var requestEntityGetTypes = new HttpEntity<String>(basicAuthHeaders());
	final var responseGetTypes = restTemplate.exchange(HTTP_127_0_0_1 + managementPort + "/actuator/health", GET, requestEntityGetTypes, String.class);
	
	final var tokenList = responseGetTypes.getHeaders().get("XSRF-TOKEN");
	final var cookies = responseGetTypes.getHeaders().get("Set-Cookie");

	// ---------------------------------------------------------------------------------------------------------------
	final var json = """
			{
			  "tesseractFolder": "/blabla",
			  "tesseractLanguage": "pt",
			  "tesseractDpi": "90"
			}""";

	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity(HTTP_127_0_0_1 + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);

	assertThat(responseUpdateTesseract.getStatusCode()) //
			.as("Update tesseract's configs with wrong values") //
			.isEqualTo(NO_CONTENT);

	// ---------------------------------------------------------------------------------------------------------------
	final var headers = basicAuthHeaders();
	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	headers.add("X-XSRF-TOKEN", tokenList.get(0));
	headers.set("Cookie", cookies.stream().collect(joining(";")));

	final var fileName = Objects.nonNull(imageFile.getFilename()) ? imageFile.getFilename() : "file";

	final var contentDisposition = builder("form-data") //
			.name("file") //
			.filename(fileName) //
			.build();

	final var fileMap = new LinkedMultiValueMap<String, String>();
	fileMap.add(CONTENT_DISPOSITION, contentDisposition.toString());
	final var fileEntity = new HttpEntity<>(imageFile.getInputStream().readAllBytes(), fileMap);

	final var body = new LinkedMultiValueMap<String, Object>();
	body.add("file", fileEntity);

	final var request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

	try {

	    // when
	    restTemplate.exchange(HTTP_127_0_0_1 + serverPort + REST_URL + "?trace=true", POST, request, String.class);

	} catch (final InternalServerError ex) {

	    // then
	    assertThat(ex.getResponseBodyAsString()) //
			    .as("Check the error while converting") //
			    .contains("TesseractNotSetException: Tessarct configuration is invalid: folder /blabla, language: pt and dpi 90") //
			    .contains("\"status\":500");
	}

    }
}
