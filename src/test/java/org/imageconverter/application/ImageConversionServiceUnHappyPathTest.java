package org.imageconverter.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.domain.convertion.ExecutionType.WEB;
import static org.imageconverter.domain.convertion.ExecutionType.WS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.imageconverter.domain.convertion.ImageConvertion;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ElementInvalidException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequest;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * Test the {@link ImageConversionService} unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("integration")
@DisplayName("Integration Test for ImageConversionService, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestInstance(PER_CLASS)
class ImageConversionServiceUnHappyPathTest {

    private final ImageConversionService imageConversionService;

    private final Resource imageFile;
    
    ImageConversionServiceUnHappyPathTest(@Autowired final ImageConversionService imageConversionService, @Value("classpath:bill.png") final Resource imageFile) {
	super();
	this.imageConversionService = imageConversionService;
	this.imageFile = imageFile;
    }

    @Order(1)
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 1L) // id '1' don't exist
    @DisplayName("try to get a image convertion by id doens't ")
    void findImageConvertionByInvalidIdTest(final Long id) {

	assertThatThrownBy(() -> imageConversionService.findById(id)) //
			.isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);

    }

//    @Test
//    @Order(2)
//    @DisplayName("get all image convertions")
//    public void getAllImageConvertionTest() throws Exception {
//
//	// already on db, due to the db-data-test.sql
//	final var id = 1000L;
//
//	final var responses = imageConversionService.findAll();
//
//	assertThat(responses).map(f -> f.id()).contains(id);
//
//	assertThat(responses).map(f -> f.text()).containsAnyOf(DB_CONVERTION_NUMBER);
//    }

    @Test
    @Order(3)
    @DisplayName("get a image convertion by invalid specification")
    void findImageConvertionByInvalidExtensionTest() {

	final var specFieldOneNotExists = (Specification<ImageConvertion>) (root, query, builder) -> builder.equal(root.get("fieldOneNotExists"), "blabla");
	final var specFieldTwoNotExists = (Specification<ImageConvertion>) (root, query, builder) -> builder.equal(root.get("fieldTwoNotExists"), "blabla");

	assertThatThrownBy(() -> {

	    imageConversionService.findBySpecification(specFieldOneNotExists.and(specFieldTwoNotExists));

	}).isInstanceOf(ElementInvalidException.class);
    }

    // ---------------------------------------------------------------------------------------------------------------

    Stream<Arguments> convertInvalidParameterData() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final ImageConverterRequest nullImageConverterRequest = null;

	return Stream.of( //
			Arguments.of(new ImageConverterRequest(null, WEB)), //
			Arguments.of(new ImageConverterRequest(multipartFile, null)), //
			Arguments.of(nullImageConverterRequest) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : request ''{0}'' ")
    @MethodSource("convertInvalidParameterData")
    @Order(4)
    @DisplayName("convert the image with invalid parameters")
    void convertInvalidParameterTest(final ImageConverterRequest request) throws IOException {

	assertThatThrownBy(() -> {

	    imageConversionService.convert(request);

	}).isInstanceOfAny(ConvertionException.class, ConstraintViolationException.class);
    }

    // ---------------------------------------------------------------------------------------------------------------

    Stream<Arguments> convertAreaInvalidParameterData() throws IOException {

	final var mockMultipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final ImageConverterRequestArea nullImageConverterRequestArea = null;

	return Stream.of( //
			Arguments.of(new ImageConverterRequestArea(null, WEB, null, null, null, null)), //
			Arguments.of(new ImageConverterRequestArea(mockMultipartFile, null, null, null, null, null)), //
			Arguments.of(new ImageConverterRequestArea(mockMultipartFile, WS, -1, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(mockMultipartFile, WS, 885, -1, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(mockMultipartFile, WS, 885, 1417, -1, 57)), //
			Arguments.of(new ImageConverterRequestArea(mockMultipartFile, WS, 885, 1417, 1426, -1)), //
			Arguments.of(nullImageConverterRequestArea) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : request ''{0}'' ")
    @MethodSource("convertAreaInvalidParameterData")
    @Order(5)
    @DisplayName("convert the image with area")
    void convertAreaInvalidParameterTest(final ImageConverterRequestArea request) {

	assertThatThrownBy(() -> {

	    imageConversionService.convert(request);

	}).isInstanceOfAny(ConvertionException.class, ConstraintViolationException.class);

    }
}
