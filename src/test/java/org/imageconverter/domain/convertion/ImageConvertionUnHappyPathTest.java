package org.imageconverter.domain.convertion;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.domain.convertion.ExecutionType.WEB;
import static org.imageconverter.domain.convertion.ExecutionType.WS;
import static org.imageconverter.domain.convertion.ImageConvertionHappyPathTest.FILE_NAME_IMAGE_PNG;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.domain.imagetype.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.util.BeanUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;

@Tag("unit")
@DisplayName("Test the image type entity, Unhappy Path :(")
@TestInstance(PER_CLASS)
public class ImageConvertionUnHappyPathTest {

    private Validator validator;

    private MockMultipartFile mockMultipartFile;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ImageTypeRespository imageTypeRespository;

    @Mock
    private ITesseract tesseractTess4j;

    @BeforeAll
    public void setUp() throws Exception {

	// ------------------------------------
	validator = buildDefaultValidatorFactory().getValidator();

	// ------------------------------------
	final var file = new File("src/test/resources/" + FILE_NAME_IMAGE_PNG);

	final var image = ImageIO.read(file);
	final var baos = new ByteArrayOutputStream();

	ImageIO.write(image, "png", baos);
	final var bytes = baos.toByteArray();

	mockMultipartFile = new MockMultipartFile("file", FILE_NAME_IMAGE_PNG, MULTIPART_FORM_DATA_VALUE, bytes);

	// ------------------------------------
	MockitoAnnotations.openMocks(this);

	BeanUtil.defineContext(applicationContext);

	when(applicationContext.getBean(ImageTypeRespository.class)) //
			.thenReturn(imageTypeRespository);

	when(applicationContext.getBean(Validator.class)) //
			.thenReturn(validator);

	when(applicationContext.getBean(Validator.class)) //
			.thenReturn(validator);

	final var imageType = Optional.of(new ImageType("png", "PNG", "Portable Network Graphics"));

	when(imageTypeRespository.findByExtension("png")) //
			.thenReturn(imageType);

	when(applicationContext.getBean(TesseractService.class)) //
			.thenReturn(new TesseractService(tesseractTess4j));

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
		    final MultipartFile data, final ExecutionType executionType, //
		    final Integer x, final Integer y, final Integer width, final Integer height) {

	assertThatThrownBy(() -> {

	    new ImageConvertion.Builder().with($ -> {
		$.data = data;
		$.executionType = executionType;
		$.x = x;
		$.y = y;
		$.width = width;
		$.height = height;
	    }).build();

	}).isInstanceOfAny(ConvertionException.class, ConstraintViolationException.class);

    }
}
