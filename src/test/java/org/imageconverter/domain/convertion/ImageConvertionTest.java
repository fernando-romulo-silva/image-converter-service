package org.imageconverter.domain.convertion;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static nl.jqno.equalsverifier.Warning.REFERENCE_EQUALITY;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.apache.commons.io.FileUtils;
import org.imageconverter.domain.imageType.ImageType;
import org.imageconverter.domain.imageType.ImageTypeRespository;
import org.imageconverter.util.BeanUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;

import net.sourceforge.tess4j.ITesseract;
import nl.jqno.equalsverifier.EqualsVerifier;

@Tag("unit")
@DisplayName("Test the image type entity")
@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ImageConvertionTest {

    private Validator validator;

    private ExecutableValidator executableValidator;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ImageTypeRespository imageTypeRespository;

    @Mock
    private ITesseract tesseractTess4j;

    @BeforeAll
    public void setUp() {
	validator = buildDefaultValidatorFactory().getValidator();

	executableValidator = buildDefaultValidatorFactory() //
			.getValidator() //
			.forExecutables();

	MockitoAnnotations.openMocks(this);

	when(applicationContext.getBean(ImageTypeRespository.class)) //
			.thenReturn(imageTypeRespository);

	when(imageTypeRespository.findByExtension("png")) //
			.thenReturn(Optional.of(new ImageType("png", "PNG", "Portable Network Graphics")));

	when(applicationContext.getBean(TesseractService.class)) //
			.thenReturn(new TesseractService(tesseractTess4j));

	BeanUtil.definedContext(applicationContext);

    }

    @Test
    @Order(1)
    @DisplayName("Test the equals And HashCode Contract")
    public void equalsAndHashCodeContractTest() {

	EqualsVerifier.forClass(ImageConvertion.class) //
			.suppress(NONFINAL_FIELDS, STRICT_INHERITANCE, REFERENCE_EQUALITY) //
			.withIgnoredAnnotations(Entity.class, Id.class, Column.class, Table.class, GeneratedValue.class, ManyToOne.class, JoinColumn.class) //
			.withOnlyTheseFields("fileName", "type") //
			.verify();

    }

    @Test
    @Order(2)
    @DisplayName("Test the toString method")
    public void toStringTest() {
	ToStringVerifier.forClass(ImageConvertion.class) //
			.withIgnoredFields("text", "created", "area") //
			.withClassName(NameStyle.SIMPLE_NAME) //
//			.withOnlyTheseFields("")
			.withFailOnExcludedFields(false) //
			.verify();
    }

    public Stream<Arguments> createValidImageConvertionData() throws IOException {

	final var url = Thread.currentThread().getContextClassLoader().getResourceAsStream("test/resources/image.png");
	
	Path resourceDirectory = Paths.get("src","test","resources", "image.png");
	
	final var image = ImageIO.read(url);
	final var baos = new ByteArrayOutputStream();
	ImageIO.write(image, "jpg", baos);
	final var bytes = baos.toByteArray();

	final var mockMultipartFile = new MockMultipartFile("fileData", "filname", "text/plain", bytes);

	return Stream.of( //
			Arguments.of(mockMultipartFile, ExecutionType.WEB) //
	);
    }

    @ParameterizedTest(name = "Pos ''{index}'':  ")
    @MethodSource("createValidImageConvertionData")
    @Order(3)
    @DisplayName("Test the imageConvertion's creation")
    public void createValidImageConvertionTest(final MultipartFile data, final ExecutionType executionType) {

	final var ic = new ImageConvertion.Builder().with($ -> {
	    $.data = data;
	    $.executionType = executionType;
	}).build();
    }
}
