package org.imageconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.imageconverter.TestConstants.HTTP_127_0_0_1;

import java.io.UnsupportedEncodingException;

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

/**
 * Test the {@link TesseractInfoService} actuator controller on unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the tesseract Health, unhappy path :( ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class TesseractHealthUnHappyTest extends BaseTesseractHealthTest {

    @Test
    @Order(1)
    @DisplayName("get actuator health invalid status")
    void findActuatorHealthInvalidTest() throws UnsupportedEncodingException {

	// given
	final var restTemplate = new RestTemplate();

	final var json = """
			{
			  "tesseractFolder": "/blabla"
			}""";

	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity(HTTP_127_0_0_1 + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);
	
	assertThat(responseUpdateTesseract.getStatusCode()) //
			.as("Updating the tesseract's configs to wrong values") //
			.isEqualTo(NO_CONTENT);

	final var requestEntity = new HttpEntity<>(csrfHeaders());

	try {

	    // when
	    restTemplate.exchange(HTTP_127_0_0_1 + managementPort + "/actuator/health", GET, requestEntity, String.class);

	} catch (final ServiceUnavailable ex) {

	    // then
	    assertThat(ex.getResponseBodyAsString()).as("Check if tesseract is out") //
			    .contains("\"status\":\"DOWN\"") //
			    .contains("\"tesseract\":{\"status\":\"DOWN\"");
	}
    }

    @Test
    @Order(2)
    @DisplayName("get actuator tesseract fail status")
    void findActuatorTesseractInvalidTest() throws UnsupportedEncodingException {

	// given
	final var restTemplate = new RestTemplate();

	final var json = """
			{
			  "tesseractFolder": "/blabla",
			  "tesseractLanguage": "pt",
			  "tesseractDpi": "90"
			}""";

	final var requestEntityUpdateTesseract = new HttpEntity<String>(json, csrfHeaders());
	final var responseUpdateTesseract = restTemplate.postForEntity(HTTP_127_0_0_1 + managementPort + "/actuator/tesseract", requestEntityUpdateTesseract, String.class);

	assertThat(responseUpdateTesseract.getStatusCode()) //
			.as("Updating the tesseract's configs to wrong values") //
			.isEqualTo(NO_CONTENT);

	final var requestEntity = new HttpEntity<>(csrfHeaders());
	
	// when
	final var response = restTemplate.exchange(HTTP_127_0_0_1 + managementPort + "/actuator/tesseract", GET, requestEntity, String.class);

	// then
	assertThat(response.getBody()).as("Check the wrong tesseract values") //
			.contains("\"tesseractInit\":\"FAIL\"") //
			.contains("\"tesseractVersion\":\"4.11\"") //
			.contains("\"tesseractLanguage\":\"pt\"") //
			.contains("\"tesseractDpi\":\"90\"");
    }

}
