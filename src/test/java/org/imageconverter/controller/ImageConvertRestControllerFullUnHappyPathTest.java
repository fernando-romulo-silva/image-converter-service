package org.imageconverter.controller;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.util.controllers.imageconverter.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.ContentDisposition.builder;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@ExtendWith(SpringExtension.class)
@Tag("acceptance")
@DisplayName("Test the image convertion, unhappy path FULL :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageConvertRestControllerFullUnHappyPathTest extends TesseractHealthTest {

    @Value("classpath:image.png")
    private Resource imageFile;

    @Test
    @Order(1)
    @DisplayName("convert the image with invalid tesseract conf")
    public void convertWithInvalidTesseractConfTest() throws Exception {

	final var restTemplate = new RestTemplate();

	// ---------------------------------------------------------------------------------------------------------------
	final var requestEntityGetTypes = new HttpEntity<String>(basicAuthHeaders());
	final var responseGetTypes = restTemplate.exchange("http://127.0.0.1:" + serverPort + REST_URL, GET, requestEntityGetTypes, String.class);
	final var tokenList = responseGetTypes.getHeaders().get("X-CSRF-TOKEN");
	final var cookies = responseGetTypes.getHeaders().get("Set-Cookie");

	// ---------------------------------------------------------------------------------------------------------------
	final var json = """
			{
			  "tesseractFolder": "/blabla",
			  "tesseractLanguage": "pt",
			  "tesseractDpi": "90"
			}""";

	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity("http://127.0.0.1:" + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);
	assertThat(responseUpdateTesseract.getStatusCode()).isEqualTo(NO_CONTENT);

	// ---------------------------------------------------------------------------------------------------------------
	final var headers = basicAuthHeaders();
	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	headers.add("X-CSRF-TOKEN", tokenList.get(0));
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

	    restTemplate.exchange("http://127.0.0.1:" + serverPort + REST_URL + "?trace=true", POST, request, String.class);

	} catch (final InternalServerError ex) {

	    final var result = ex.getResponseBodyAsString();

	    assertThat(result).contains("TesseractNotSetException: Tessarct configuration is invalid: folder /blabla, language: pt and dpi 90");
	    assertThat(result).contains("\"status\":500");
	}

    }
}
