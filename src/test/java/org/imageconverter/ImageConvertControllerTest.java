package org.imageconverter;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.util.controllers.ImageConverterConst.BASE_URL;
import static org.imageconverter.util.controllers.ImageConverterConst.REST_URL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;

import java.io.File;

import org.imageconverter.util.controllers.ImageConverterResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Tag("integration")
public class ImageConvertControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate(new RestTemplateBuilder());

    @Test
    public void pingTest() throws RestClientException {

	final var result = restTemplate.<String>getForEntity("http://localhost:" + port + BASE_URL + REST_URL + "/ping", String.class);

	assertThat(HttpStatus.OK.value()).isEqualTo(result.getStatusCodeValue());
    }

    @Test
    public void convertTest() throws RestClientException {

	final var classLoader = ImageConvertControllerTest.class.getClassLoader();

	final var imageFileData = new File(classLoader.getResource("image.png").getFile());

	final var parametersBody = new LinkedMultiValueMap<String, Object>();
	parametersBody.add("file", new FileSystemResource(imageFileData));

	final var headers = new HttpHeaders();
	headers.set("Content-Type", "multipart/form-data");
	headers.set("Accept", "*/*");

	final var httpEntity = new HttpEntity<MultiValueMap<String, Object>>(parametersBody, headers);

	final var result = restTemplate.<String>postForEntity("http://localhost:" + port + BASE_URL + REST_URL, httpEntity, String.class);

	assertThat(CREATED.value()) //
			.isEqualTo(result.getStatusCodeValue());
    }

    @Test
    public void convertWithAreaTest() throws RestClientException {

	final var classLoader = ImageConvertControllerTest.class.getClassLoader();

	final var imageFileData = new File(classLoader.getResource("image.png").getFile());

	final var parametersBody = new LinkedMultiValueMap<String, Object>();
	parametersBody.add("file", new FileSystemResource(imageFileData)); // load file into parameter
	parametersBody.add("x", 885);
	parametersBody.add("y", 1417);
	parametersBody.add("width", 1426);
	parametersBody.add("height", 57);

	final var headers = new HttpHeaders();
	headers.set("Content-Type", "multipart/form-data");
	headers.set("Accept", "*/*");

	final var httpEntity = new HttpEntity<MultiValueMap<String, Object>>(parametersBody, headers);

	final var resultEntity = restTemplate.<ImageConverterResponse>postForEntity("http://localhost:" + port + BASE_URL + REST_URL + "/area", httpEntity, ImageConverterResponse.class);

	assertThat(CREATED.value()) //
			.isEqualTo(resultEntity.getStatusCodeValue());

	assertThat("03399905748110000007433957701015176230000017040") //
			.isEqualTo(deleteWhitespace(resultEntity.getBody().text()).replaceAll("[^x0-9]", ""));
    }

}
