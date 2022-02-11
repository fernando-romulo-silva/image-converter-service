package org.imageconverter.domain.convertion;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.domain.convertion.ExecutionType.WEB;
import static org.imageconverter.domain.convertion.ExecutionType.WS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.web.multipart.MultipartFile;

@Tag("unit")
@DisplayName("Test the image type entity, Unhappy Path :(")
@TestInstance(PER_CLASS)
public class ImageConvertionUnHappyPathTest extends ImageConvertionConfigTest {

    @BeforeAll
    public void setUp() throws Exception {

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

    public Stream<Arguments> createInvalidImageConvertionData() throws IOException {

	return Stream.of( //
			Arguments.of(null, WEB, null, null, null, null), //
			Arguments.of(mockMultipartFile, null, null, null, null, null), //
			Arguments.of(mockMultipartFile, WS, -1, 1417, 1426, 57), //
			Arguments.of(mockMultipartFile, WS, 885, -1, 1426, 57), //
			Arguments.of(mockMultipartFile, WS, 885, 1417, -1, 57), //
			Arguments.of(mockMultipartFile, WS, 885, 1417, 1426, -1), //
			Arguments.of(mockMultipartFile, WS, 885, 1417, 1426, 57) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : executionType ''{1}'' ")
    @MethodSource("createInvalidImageConvertionData")
    @Order(1)
    @DisplayName("Test the imageConvertion's creation with invalid values")
    public void createInvalidImageConvertionTest( //
		    final MultipartFile file, final ExecutionType executionType, //
		    final Integer xAxis, final Integer yAxis, final Integer width, final Integer height) {

	assertThatThrownBy(() -> new ImageConvertion.Builder().with($ -> {
	    $.file = file;
	    $.executionType = executionType;
	    $.xAxis = xAxis;
	    $.yAxis = yAxis;
	    $.width = width;
	    $.height = height;
	}).build()

	).isInstanceOfAny(ConvertionException.class, ConstraintViolationException.class);

    }
}
