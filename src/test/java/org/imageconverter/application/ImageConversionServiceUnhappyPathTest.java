package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.domain.conversion.ExecutionType.PAGE;
import static org.imageconverter.domain.conversion.ExecutionType.REST;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.imageconverter.domain.conversion.ImageConversion;
import org.imageconverter.infra.exception.ConversionException;
import org.imageconverter.infra.exception.CsvFileNoDataException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequest;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestArea;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
class ImageConversionServiceUnhappyPathTest {

    private final ImageConversionService imageConversionService;

    private final Resource imageFile;

    private final Pageable pageable = PageRequest.of(0, 10);

    ImageConversionServiceUnhappyPathTest( //
		    @Autowired //
		    final ImageConversionService imageConversionService, //
		    //
		    @Value("classpath:images/bill01.png") //
		    final Resource imageFile) {
	super();
	this.imageConversionService = imageConversionService;
	this.imageFile = imageFile;
    }

    @Order(1)
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 1L) // id '1' don't exist
    @DisplayName("try to get a image conversion by id doens't ")
    void findImageConversionByInvalidIdTest(final Long id) {

	assertThatThrownBy(() -> imageConversionService.findById(id)) //
			.as(format("Check invalid values of id ''{0}''", id)) //
			.isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);

    }

    @Test
    @Order(2)
    @DisplayName("get a image conversion by invalid specification")
    void findImageConversionByInvalidExtensionTest() {

	final var specFieldOneNotExists = (Specification<ImageConversion>) (root, query, builder) -> builder.equal(root.get("fieldOneNotExists"), "blabla");
	final var specFieldTwoNotExists = (Specification<ImageConversion>) (root, query, builder) -> builder.equal(root.get("fieldTwoNotExists"), "blabla");

	assertThatThrownBy(() -> imageConversionService.findBySpecification(specFieldOneNotExists.and(specFieldTwoNotExists), pageable))
		.as(format("Check invalid Specification")) //
		.isInstanceOf(ElementInvalidException.class);
    }

    // ---------------------------------------------------------------------------------------------------------------

    Stream<Arguments> convertInvalidParameterData() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final ImageConverterRequest nullImageConverterRequest = null;

	final var filename = multipartFile.getOriginalFilename();

	return Stream.of( //
			Arguments.of(new ImageConverterRequest(null, multipartFile.getBytes(), PAGE)), //
			Arguments.of(new ImageConverterRequest(filename, null, PAGE)), //
			Arguments.of(new ImageConverterRequest(filename, multipartFile.getBytes(), null)), //
			Arguments.of(nullImageConverterRequest) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : request ''{0}'' ")
    @MethodSource("convertInvalidParameterData")
    @Order(3)
    @DisplayName("convert the image with invalid parameters")
    void convertInvalidParameterTest(final ImageConverterRequest request) throws IOException {

	assertThatThrownBy(() -> imageConversionService.convert(request))
		.as(format("Check invalid request ''{0}''", request)) //
		.isInstanceOfAny(ConversionException.class, ConstraintViolationException.class);
    }

    // ---------------------------------------------------------------------------------------------------------------

    Stream<Arguments> convertAreaInvalidParameterData() throws IOException {

	final var multipartFile = new MockMultipartFile("file", imageFile.getFilename(), MULTIPART_FORM_DATA_VALUE, imageFile.getInputStream());

	final ImageConverterRequestArea nullImageConverterRequestArea = null;

	final var fileName = multipartFile.getOriginalFilename();

	final var fileBytes = multipartFile.getBytes();

	return Stream.of( //
			Arguments.of(new ImageConverterRequestArea(null, fileBytes, REST, 885, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(StringUtils.EMPTY, fileBytes, REST, 885, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, null, PAGE, 885, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, new byte[0], PAGE, 885, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, fileBytes, null, null, null, null, null)), //
			Arguments.of(new ImageConverterRequestArea(fileName, fileBytes, REST, -1, 1417, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, fileBytes, REST, 885, -1, 1426, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, fileBytes, REST, 885, 1417, -1, 57)), //
			Arguments.of(new ImageConverterRequestArea(fileName, fileBytes, REST, 885, 1417, 1426, -1)), //
			Arguments.of(nullImageConverterRequestArea) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : request ''{0}'' ")
    @MethodSource("convertAreaInvalidParameterData")
    @Order(4)
    @DisplayName("convert the image with area")
    void convertAreaInvalidParameterTest(final ImageConverterRequestArea request) {

	assertThatThrownBy(() -> imageConversionService.convert(request))
		.as(format("Check invalid request area ''{0}''", request)) //
		.isInstanceOfAny(ConversionException.class, ConstraintViolationException.class);

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 1L) // id '1' don't exist
    @Order(7)
    @DisplayName("Try to delete a image type that doesn't exist")
    void deleteImageConversionDoesNotExistTest(final Long id) {

	assertThatThrownBy(() -> imageConversionService.deleteImageConversion(id))
		.as(format("Check if throw a exception on invalid delete, id ''{0}''", id)) //
		.isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);
    }
    
    @Test
    @Order(8)
    @DisplayName("Test a creation image conversion csv file empty")
    void createImageConversionCsvFileEmptyTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;
	
	// given
	imageConversionService.deleteImageConversion(id);
	
	// when
	assertThatThrownBy(() ->  imageConversionService.findBySpecificationToCsv(null))
		.as(format("Check if throw a exception on file generety empty file")) //
		// then
		.isInstanceOfAny(CsvFileNoDataException.class);
	
    }
}
