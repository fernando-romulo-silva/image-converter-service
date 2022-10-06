package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.math.NumberUtils.LONG_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;

import org.imageconverter.TestConstants;
import org.imageconverter.domain.conversion.ExecutionType;
import org.imageconverter.domain.conversion.ImageConversion;
import org.imageconverter.infra.exception.ElementNotFoundException;
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

    ImageConversionServiceHappyPathTest(@Autowired final ImageConversionService imageConversionService, @Value("classpath:bill01.png") final Resource imageFile) {
	super();
	this.imageConversionService = imageConversionService;
	this.imageFile = imageFile;
    }

    @Test
    @Order(1)
    @DisplayName("get a image conversion by id")
    void findImageConversionByIdTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var response = imageConversionService.findById(id);

	assertThat(response) //
			.as(format("Check the 'response' has fileName ''{0}'' and conversion txt ''{1}''", id, TestConstants.DB_CONVERSION_NUMBER)) //
			.extracting( //
					$ -> $.id(), //
					$ -> $.text().replaceAll("[^x0-9]", "") //
			).containsExactly(id, TestConstants.DB_CONVERSION_NUMBER) //
	;
    }

    @Test
    @Order(2)
    @DisplayName("get all image conversions")
    void findAllImageConversionTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var responses = imageConversionService.findAll();

	assertThat(responses) //
			.as(format("Check if there's a id ''{0}'' on the result", id))//
			.map(imageConverterResponse -> imageConverterResponse.id()) //
			.contains(id);

	assertThat(responses) //
			.as(format("Check if there's a text conversion ''{0}'' on the result", id))//
			.map(imageConverterResponse -> imageConverterResponse.text()) //
			.containsAnyOf(TestConstants.DB_CONVERSION_NUMBER);
    }

    @Test
    @Order(3)
    @DisplayName("get a image conversion by specification")
    void findImageConversionBySpecificationTest() {

	// already on db, due to the db-data-test.sql
	final var fileName = "image_test.jpg";

	// Specification<ImageConversion> equalsFileName = (rt, cq, cb) -> cb.equal(rt.get("fileName"), fileName);

	final var responses1 = imageConversionService.findBySpecification(equalsFileName(fileName));

	assertThat(responses1) //
			.as(format("Check if there's a response file name ''{0}'' on the result", fileName))//
			.map(imageConverterResponse -> imageConverterResponse.fileName()) //
			.containsAnyOf(fileName);

	final var responses2 = imageConversionService.findBySpecification(null);

	final var responseQty = 1;

	assertThat(responses2) //
			.as(format("Check if the resutl's qty is ''{0}''", responseQty))//
			.hasSize(responseQty);
    }

    static Specification<ImageConversion> equalsFileName(final String fileName) {
	return (root, query, builder) -> builder.equal(root.get("fileName"), fileName);
    }

    @Test
    @Order(4)
    @DisplayName("convert image")
    void convertTest() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequest(multipartFile.getOriginalFilename(), multipartFile.getBytes(), ExecutionType.WS);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()) //
			.as(format("Check if the response's id is greater than Zero ''{0}''", response.id()))//
			.isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as(format("Check if the response's text is equal to ''{0}''", TestConstants.IMAGE_PNG_CONVERSION_NUMBER))//
			.containsIgnoringCase(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);
    }

    @Test
    @Order(5)
    @DisplayName("convert image with area")
    void convertAreaTest() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequestArea(multipartFile.getOriginalFilename(), multipartFile.getBytes(), ExecutionType.BATCH, 885, 1417, 1426, 57);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()) //
			.as(format("Check if the response's id is greater than Zero ''{0}''", response.id()))//
			.isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.as(format("Check if the response's text is equal to ''{0}''", TestConstants.IMAGE_PNG_CONVERSION_NUMBER))//
			.containsIgnoringCase(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);
    }
    
    @Test
    @Order(6)
    @DisplayName("Delete a image conversion")
    void deleteImageTypeTest() throws IOException {

	// given
	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequestArea(multipartFile.getOriginalFilename(), multipartFile.getBytes(), ExecutionType.WEB, 885, 1417, 1426, 57);

	final var createResponse = imageConversionService.convert(request);

	// ----------------------------------------------------------

	// when
	imageConversionService.deleteImageConversion(createResponse.id());

	// ----------------------------------------------------------

	// then
	assertThatThrownBy(() -> {

	    imageConversionService.findById(createResponse.id()); //

	}) //
			.as(format("Check the ImageConversion id ''{0}'' was deleted", createResponse.id()))//
			.isInstanceOfAny(ElementNotFoundException.class);
    }
}
