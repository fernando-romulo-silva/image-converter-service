package org.imageconverter.application;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.math.NumberUtils.LONG_ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.domain.convertion.ExecutionType.BATCH;
import static org.imageconverter.domain.convertion.ExecutionType.WS;
import static org.imageconverter.domain.convertion.ImageConvertionHappyPathTest.DB_CONVERTION_NUMBER;
import static org.imageconverter.domain.convertion.ImageConvertionHappyPathTest.IMAGE_PNG_CONVERTION_NUMBER;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@ActiveProfiles("test")
@Tag("integration")
@DisplayName("Integration Test for ImageConversionService, happy path :D ")
//
@SpringBootTest
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
public class ImageConversionServiceHappyPathTest {

    @Autowired
    private ImageConversionService imageConversionService;

    @Value("classpath:image.png")
    private Resource imageFile;

    @Test
    @Order(1)
    @DisplayName("get a image convertion by id")
    public void getImageConvertionByIdTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var response = imageConversionService.findById(id);

	assertThat(response.id()).isEqualTo(id);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.containsIgnoringCase(DB_CONVERTION_NUMBER);
    }

    @Test
    @Order(2)
    @DisplayName("get all image convertions")
    public void getAllImageConvertionTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var responses = imageConversionService.findAll();

	assertThat(responses).map(f -> f.id()).contains(id);

	assertThat(responses).map(f -> f.text()).containsAnyOf(DB_CONVERTION_NUMBER);
    }

    static Specification<ImageConvertion> equalsFileName(final String fileName) {
	return (book, cq, cb) -> cb.equal(book.get("fileName"), fileName);
    }

    @Test
    @Order(3)
    @DisplayName("get a image convertion by search")
    public void getImageTypeByExtensionTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var fileName = "image_test.jpg";

	// Specification<ImageConvertion> spec = (book, cq, cb) -> cb.equal(book.get("fileName"), fileName);

	final var responses1 = imageConversionService.findBySpecification(equalsFileName(fileName));

	assertThat(responses1).map(f -> f.fileName()).containsAnyOf(fileName);
	
	final var responses2 = imageConversionService.findBySpecification(null);
	
	assertThat(responses2).hasSize(1);

    }

    @Test
    @Order(4)
    @DisplayName("convert the image")
    public void convertTest() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequest(multipartFile, WS);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.containsIgnoringCase(IMAGE_PNG_CONVERTION_NUMBER);
    }

    @Test
    @Order(5)
    @DisplayName("convert the image with area")
    public void convertAreaTest() throws Exception {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final var request = new ImageConverterRequestArea(multipartFile, BATCH, 885, 1417, 1426, 57);

	final var response = imageConversionService.convert(request);

	assertThat(response.id()).isGreaterThan(LONG_ZERO);

	assertThat(deleteWhitespace(response.text()).replaceAll("[^x0-9]", "")) //
			.isEqualTo(IMAGE_PNG_CONVERTION_NUMBER);
    }
}
