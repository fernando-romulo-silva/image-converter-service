package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.math.NumberUtils.LONG_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;

import org.imageconverter.TestConstants;
import org.imageconverter.domain.convertion.ExecutionType;
import org.imageconverter.domain.convertion.ImageConvertion;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequest;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * Test the {@link ImageConversionService} happy path
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("integration")
@DisplayName("Integration Test for ImageConversionService, happy path :D ")
class ImageConversionServiceHappyPathTest {

    private final ImageConversionService imageConversionService;

    private final Resource imageFile;

    ImageConversionServiceHappyPathTest(@Autowired final ImageConversionService imageConversionService, @Value("classpath:bill.png") final Resource imageFile) {
	super();
	this.imageConversionService = imageConversionService;
	this.imageFile = imageFile;
    }

    @Test
    @Order(1)
    @DisplayName("get a image convertion by id")
    void findImageConvertionByIdTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var response = imageConversionService.findById(id);

	assertThat(response) //
			.as(format("Check the 'response' has fileName ''{0}'' and convertion txt ''{1}''", id, TestConstants.DB_CONVERTION_NUMBER)) //
			.extracting( //
					$ -> $.id(), //
					$ -> $.text().replaceAll("[^x0-9]", "") //
			).containsExactly(id, TestConstants.DB_CONVERTION_NUMBER) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("get all image convertions")
    void findAllImageConvertionTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var responses = imageConversionService.findAll();

	assertThat(responses).map(imageConverterResponse -> imageConverterResponse.id()).contains(id);

	assertThat(responses).map(imageConverterResponse -> imageConverterResponse.text()).containsAnyOf(TestConstants.DB_CONVERTION_NUMBER);
    }

    @Test
    @Order(3)
    @DisplayName("get a image convertion by specification")
    void findImageConvertionBySpecificationTest() {

	// already on db, due to the db-data-test.sql
	final var fileName = "image_test.jpg";

	// Specification<ImageConvertion> spec = (rt, cq, cb) -> cb.equal(rt.get("fileName"), fileName);

	final var responses1 = imageConversionService.findBySpecification(equalsFileName(fileName));

	assertThat(responses1).map(imageConverterResponse -> imageConverterResponse.fileName()).containsAnyOf(fileName);

	final var responses2 = imageConversionService.findBySpecification(null);

	assertThat(responses2).hasSize(1);
    }

    static Specification<ImageConvertion> equalsFileName(final String fileName) {
	return (root, query, builder) -> builder.equal(root.get("fileName"), fileName);
    }

    @Test
    @Order(4)
    @DisplayName("convert image")
    void convertTest() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequest(multipartFile.getOriginalFilename(), multipartFile.getBytes(), ExecutionType.WS);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.containsIgnoringCase(TestConstants.IMAGE_PNG_CONVERTION_NUMBER);
    }

    @Test
    @Order(5)
    @DisplayName("convert image with area")
    void convertAreaTest() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequestArea(multipartFile.getOriginalFilename(), multipartFile.getBytes(), ExecutionType.BATCH, 885, 1417, 1426, 57);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.isEqualTo(TestConstants.IMAGE_PNG_CONVERTION_NUMBER);
    }
}
