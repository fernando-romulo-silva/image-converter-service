package org.imageconverter.controller;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.util.controllers.ImageConverterConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;

import org.imageconverter.util.controllers.ImageConverterResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
//
@Tag("acceptance")
@DisplayName("Test the image convertion, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
@Disabled("Because we use mvc version")
public class ImageConvertRestControllerHappyPathTestOld {

    @LocalServerPort
    private int port;

    @Value("classpath:image.png")
    private Resource imageFile;

//    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate(new RestTemplateBuilder().basicAuthentication("user", "password"));

    @Test
    @Order(1)
    @DisplayName("convert the image test")
    public void convertTest() throws Exception {

	final var parametersBody = new LinkedMultiValueMap<String, Object>();
	parametersBody.add("file", new FileSystemResource(imageFile.getFile()));

	final var headers = new HttpHeaders();
	headers.set("Content-Type", "multipart/form-data");
	headers.set("Accept", "*/*");

	final var httpEntity = new HttpEntity<MultiValueMap<String, Object>>(parametersBody, headers);

	final var result = restTemplate.<String>postForEntity("http://localhost:" + port + REST_URL, httpEntity, String.class);

	assertThat(CREATED.value()) //
			.isEqualTo(result.getStatusCodeValue());
    }

    @Test
    @Order(2)
    @DisplayName("convert all image test with area")
    public void convertWithAreaTest() throws Exception {

	final var parametersBody = new LinkedMultiValueMap<String, Object>();
	parametersBody.add("file", new FileSystemResource(imageFile.getFile()));
	parametersBody.add("x", 885);
	parametersBody.add("y", 1417);
	parametersBody.add("width", 1426);
	parametersBody.add("height", 57);

	final var headers = new HttpHeaders();
	headers.set("Content-Type", "multipart/form-data");
	headers.set("Accept", "*/*");

	final var httpEntity = new HttpEntity<MultiValueMap<String, Object>>(parametersBody, headers);

	final var resultEntity = restTemplate.<ImageConverterResponse>postForEntity("http://localhost:" + port + REST_URL + "/area", httpEntity, ImageConverterResponse.class);

	assertThat(CREATED.value()) //
			.isEqualTo(resultEntity.getStatusCodeValue());

	assertThat("03399905748110000007433957701015176230000017040") //
			.isEqualTo(deleteWhitespace(resultEntity.getBody().text()).replaceAll("[^x0-9]", ""));
    }

}