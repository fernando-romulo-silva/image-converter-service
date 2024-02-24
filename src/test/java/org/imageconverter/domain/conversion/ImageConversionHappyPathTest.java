package org.imageconverter.domain.conversion;

import static com.jparams.verifier.tostring.NameStyle.SIMPLE_NAME;
import static java.text.MessageFormat.format;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static nl.jqno.equalsverifier.Warning.REFERENCE_EQUALITY;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.domain.conversion.ExecutionType.WEB;
import static org.imageconverter.domain.conversion.ExecutionType.WS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.imageconverter.TestConstants;
import org.imageconverter.domain.imagetype.ImageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Test the {@link ImageConversion} class on happy path.
 * 
 * @author Fernando Romulo da Silva
 */
@Tag("unit")
@DisplayName("Test the image type entity, happy Path :) ")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class ImageConversionHappyPathTest extends ImageConversionConfigTest {

    @BeforeAll
    void setUp() throws Exception {
	setUpSuper();

	when(applicationContext.getBean(TesseractService.class)) //
			.thenReturn(new TesseractService());
	
	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any())) //
			.thenReturn(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);

	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any(), ArgumentMatchers.<Rectangle>any())) //
			.thenReturn(TestConstants.IMAGE_PNG_CONVERSION_NUMBER);

	final var imageType = Optional.of(new ImageType("png", "PNG", "Portable Network Graphics"));

	when(imageTypeRespository.findByExtension("png")) //
			.thenReturn(imageType);
    }

    @Test
    @Order(1)
    @DisplayName("Test the equals And HashCode Contract")
    void tryEqualsAndHashCodeContractTest() { // NOPMD - JUnitTestsShouldIncludeAssert: EqualsVerifier already do it

	EqualsVerifier.forClass(ImageConversion.class) //
			.suppress(NONFINAL_FIELDS, STRICT_INHERITANCE, REFERENCE_EQUALITY) //
			.withIgnoredAnnotations(Entity.class, Id.class, Column.class, Table.class, GeneratedValue.class, ManyToOne.class, JoinColumn.class) //
			.withOnlyTheseFields("id") //
			.verify();

    }

    @Test
    @Order(2)
    @DisplayName("Test the toString method")
    void tryToStringTest() { // NOPMD - JUnitTestsShouldIncludeAssert: ToStringVerifier already do it

	ToStringVerifier.forClass(ImageConversion.class) //
			.withIgnoredFields("text", "created", "area") //
			.withClassName(SIMPLE_NAME) //
			.withFailOnExcludedFields(false) //
			.verify();
    }

    Stream<Arguments> createValidImageConversionData() throws IOException {

	final var fileName = mockMultipartFile.getOriginalFilename();
	final var fileBytes = mockMultipartFile.getBytes();

	return Stream.of( //
			Arguments.of(fileName, fileBytes, WEB, false, null, null, null, null), //
			Arguments.of(fileName, fileBytes, WS, true, 885, 1417, 1426, 57) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : fileName ''{0}'', type ''{2}'' ")
    @MethodSource("createValidImageConversionData")
    @Order(3)
    @DisplayName("Test the imageConversion's creation")
    void createValidImageConversionTest( //
		    // given
		    final String fileName, final byte[] fileContent, final ExecutionType executionType, //
		    final boolean area, final Integer xAxis, final Integer yAxis, final Integer width, final Integer height) {

	// when
	final var imageConversionBuilder = new ImageConversion.Builder().with($ -> {
	    $.fileName = fileName;
	    $.fileContent = fileContent;
	    $.executionType = executionType;
	    $.xAxis = xAxis;
	    $.yAxis = yAxis;
	    $.width = width;
	    $.height = height;
	});

	final var imageConversion = imageConversionBuilder.build();

	final var fileExtension = getExtension(fileName);

	// then
	assertThat(imageConversion) //
			.as(format("Check the fileName ''{0}'', executionType ''{1}'', area ''{2}'' and fileExtension ''{3}''", fileName, executionType, area, fileExtension)) //
			.extracting("fileName", "type", "area") //
			.containsExactly(fileName, executionType, area) //
			.extracting(extension -> imageConversion.getFileType().getExtension()) //
			.containsAnyOf(fileExtension);

    }
}
