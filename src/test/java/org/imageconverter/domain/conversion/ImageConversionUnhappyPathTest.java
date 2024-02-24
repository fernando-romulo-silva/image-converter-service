package org.imageconverter.domain.conversion;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.domain.conversion.ExecutionType.WEB;
import static org.imageconverter.domain.conversion.ExecutionType.WS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.infra.exception.ConversionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;

/**
 * Test the {@link ImageConversion} class on unhappy path.
 * 
 * @author Fernando Romulo da Silva
 */
@Tag("unit")
@DisplayName("Test the image type entity, Unhappy Path :(")
@TestInstance(PER_CLASS)
class ImageConversionUnhappyPathTest extends ImageConversionConfigTest {

    @BeforeAll
    void setUp() throws Exception {

	setUpSuper();

	final var imageType = Optional.of(new ImageType("png", "PNG", "Portable Network Graphics"));

	when(imageTypeRespository.findByExtension("png")) //
			.thenReturn(imageType);

	when(applicationContext.getBean(TesseractService.class)) //
			.thenReturn(new TesseractService());

	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any())) //
			.thenReturn(null);

	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any(), ArgumentMatchers.<Rectangle>any())) //
			.thenReturn(EMPTY);
    }

    Stream<Arguments> createInvalidImageConversionData() throws IOException {

	final var fileName = mockMultipartFile.getOriginalFilename();
	final var fileBytes = mockMultipartFile.getBytes();

	return Stream.of( //
			Arguments.of(null, fileBytes, WEB, null, null, null, null), //
			Arguments.of(fileName, null, WEB, null, null, null, null), //
			Arguments.of(fileName, fileBytes, null, null, null, null, null), //
			Arguments.of(fileName, fileBytes, WS, -1, 1417, 1426, 57), //
			Arguments.of(fileName, fileBytes, WS, 885, -1, 1426, 57), //
			Arguments.of(fileName, fileBytes, WS, 885, 1417, -1, 57), //
			Arguments.of(fileName, fileBytes, WS, 885, 1417, 1426, -1), //
			Arguments.of(fileName, fileBytes, WS, 885, 1417, 1426, 57) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : fileName ''{0}'', type ''{2}'' ")
    @MethodSource("createInvalidImageConversionData")
    @DisplayName("Test the imageConversion's creation with invalid values")
    void createInvalidImageConversionTest( //
		    // given
		    final String fileName, final byte[] fileContent, final ExecutionType executionType, //
		    final Integer xAxis, final Integer yAxis, final Integer width, final Integer height) {

	final var area = Objects.nonNull(xAxis) ? "x " + xAxis + ", y " + yAxis + ", width " + width + ", height " + height : "";

	// when
	assertThatThrownBy(() -> new ImageConversion.Builder().with($ -> {
	    $.fileName = fileName;
	    $.fileContent = fileContent;
	    $.executionType = executionType;
	    $.xAxis = xAxis;
	    $.yAxis = yAxis;
	    $.width = width;
	    $.height = height;
	}).build()) //
	// then
	.as(format("Check the invalid fileName ''{0}'', executionType ''{1}'' and area ''{2}'' ", fileName, executionType, area)) //
	.isInstanceOfAny(ConversionException.class, ConstraintViolationException.class);
    }
}
