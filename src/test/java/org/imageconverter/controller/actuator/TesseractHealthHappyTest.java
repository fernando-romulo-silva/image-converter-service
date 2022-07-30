package org.imageconverter.controller.actuator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.TestConstants.HTTP_127_0_0_1;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.UnsupportedEncodingException;

import org.imageconverter.config.health.TesseractInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.web.client.RestTemplate;

/**
 * Test the {@link TesseractInfoService} actuator controller on happy path.
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest( //
		webEnvironment = WebEnvironment.RANDOM_PORT //
//		properties = { "management.server.port: " } //

)
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("acceptance")
@DisplayName("Test the tesseract Health, happy path :D ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
class TesseractHealthHappyTest extends BaseTesseractHealthTest {

    @Test
    @Order(1)
    @DisplayName("get actuator health status")
    void findActuatorHealthTest() throws UnsupportedEncodingException {

	// given
	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	
	// when
	final var response = restTemplate.exchange(HTTP_127_0_0_1 + managementPort + "/actuator/health", GET, requestEntity, String.class);
	final var body = response.getBody();

	// then
	assertThat(body).as("Check if tesseract status is ok") //
			.contains("\"status\":\"UP\"") //
			.contains("\"tesseract\":{\"status\":\"UP\"}");
    }

    @Test
    @Order(2)
    @DisplayName("get actuator tesseract status")
    void findActuatorTesseractTest() throws UnsupportedEncodingException {

	// given
	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	
	// when
	final var response = restTemplate.exchange(HTTP_127_0_0_1 + managementPort + "/actuator/tesseract", GET, requestEntity, String.class);
	final var body = response.getBody();

	// then
	assertThat(body).as("Check the tesseract properties") //
			.contains("\"tesseractInit\":\"SUCCESSFUL\"") //
			.contains("\"tesseractVersion\":\"4.11\"") //
			.contains("\"tesseractLanguage\":\"eng\"") //
			.contains("\"tesseractDpi\":\"100\"");

    }

    @Test
    @Order(3)
    @DisplayName("post actuator refresh")
    void postActuatorRefreshTest() throws UnsupportedEncodingException {

	// given
	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	
	// when
	final var response = restTemplate.postForEntity(HTTP_127_0_0_1 + managementPort + "/actuator/refresh", requestEntity, Void.class);

	// then
	assertThat(response.getStatusCode()) //
			.as("Check the updating tesseract configs") //
			.isEqualTo(OK);
    }

}
