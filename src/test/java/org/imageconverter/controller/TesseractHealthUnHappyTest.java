package org.imageconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the tesseract Health, unhappy path :( ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class TesseractHealthUnHappyTest extends AbstractTesseractHealthTest {

    @Test
    @Order(1)
    @DisplayName("get actuator health invalid status")
    void getActuatorHealthInvalidTest() throws Exception {

	final var restTemplate = new RestTemplate();

	final var json = """
			  {
			    "tesseractFolder": "/blabla"
			  }""";
	
	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity("http://127.0.0.1:" + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);
	assertThat(responseUpdateTesseract.getStatusCode()).isEqualTo(NO_CONTENT);

	final var requestEntity = new HttpEntity<>(csrfHeaders());

	try {

	    restTemplate.exchange("http://127.0.0.1:" + managementPort + "/actuator/health", GET, requestEntity, String.class);

	} catch (final ServiceUnavailable ex) {

	    final var body = ex.getResponseBodyAsString();

	    assertThat(body).contains("\"status\":\"DOWN\"");
	    assertThat(body).contains("\"tesseract\":{\"status\":\"DOWN\"");
	}
    }

    @Test
    @Order(2)
    @DisplayName("get actuator tesseract fail status")
    void getActuatorTesseractInvalidTest() throws Exception {

	final var restTemplate = new RestTemplate();

	final var json = """
			  {
			    "tesseractFolder": "/blabla",
			    "tesseractLanguage": "pt",
			    "tesseractDpi": "90"			
			  }""";
	
	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity("http://127.0.0.1:" + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);
	assertThat(responseUpdateTesseract.getStatusCode()).isEqualTo(NO_CONTENT);
	
	
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	final var response = restTemplate.exchange("http://127.0.0.1:" + managementPort + "/actuator/tesseract", GET, requestEntity, String.class);

	final var body = response.getBody();

	assertThat(body).contains("\"tesseractInit\":\"FAIL\"");
	assertThat(body).contains("\"tesseractVersion\":\"4.11\"");
	assertThat(body).contains("\"tesseractLanguage\":\"pt\"");
	assertThat(body).contains("\"tesseractDpi\":\"90\"");
    }

}
