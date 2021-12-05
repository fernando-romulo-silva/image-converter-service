package org.imageconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
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
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
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
public class TesseractHealthHappyTest extends TesseractHealthTest {

    @Test
    @Order(1)
    @DisplayName("get actuator health status")
    public void getActuatorHealthTest() throws Exception {

	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	final var response = restTemplate.exchange("http://127.0.0.1:" + managementPort + "/actuator/health", GET, requestEntity, String.class);

	final var body = response.getBody();

	assertThat(body).contains("\"status\":\"UP\"");
	assertThat(body).contains("\"tesseract\":{\"status\":\"UP\"}");
    }

    @Test
    @Order(2)
    @DisplayName("get actuator tesseract status")
    public void getActuatorTesseractTest() throws Exception {

	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	final var response = restTemplate.exchange("http://127.0.0.1:" + managementPort + "/actuator/tesseract", GET, requestEntity, String.class);

	final var body = response.getBody();

	assertThat(body).contains("\"tesseractInit\":\"SUCCESSFUL\"");
	assertThat(body).contains("\"tesseractVersion\":\"4.11\"");
	assertThat(body).contains("\"tesseractLanguage\":\"eng\"");
	assertThat(body).contains("\"tesseractDpi\":\"100\"");

    }

    @Test
    @Order(3)
    @DisplayName("post actuator refresh")
    public void postActuatorRefreshTest() throws Exception {

	final var restTemplate = new RestTemplate();
	final var requestEntity = new HttpEntity<>(csrfHeaders());
	final var response = restTemplate.postForEntity("http://127.0.0.1:" + managementPort + "/actuator/refresh", requestEntity, Void.class);

	assertThat(response.getStatusCode()).isEqualTo(OK);
    }

}
