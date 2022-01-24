package org.imageconverter.domain.convertion;

import static com.jparams.verifier.tostring.NameStyle.SIMPLE_NAME;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static nl.jqno.equalsverifier.Warning.REFERENCE_EQUALITY;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.domain.convertion.ExecutionType.WEB;
import static org.imageconverter.domain.convertion.ExecutionType.WS;
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

import org.imageconverter.domain.imagetype.ImageType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.web.multipart.MultipartFile;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

@Tag("unit")
@DisplayName("Test the image type entity, happy Path :) ")
@TestInstance(PER_CLASS)
class ImageConvertionHappyPathTest extends ImageConvertionConfigTest implements TestConstants {

    @BeforeAll
    public void setUp() throws Exception {
	setUpSuper();

	when(applicationContext.getBean(TesseractService.class)) //
			.thenReturn(new TesseractService());

	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any())) //
			.thenReturn(IMAGE_PNG_CONVERTION_NUMBER);

	when(tesseractTess4j.doOCR(ArgumentMatchers.<BufferedImage>any(), ArgumentMatchers.<Rectangle>any())) //
			.thenReturn(IMAGE_PNG_CONVERTION_NUMBER);

	final var imageType = Optional.of(new ImageType("png", "PNG", "Portable Network Graphics"));

	when(imageTypeRespository.findByExtension("png")) //
			.thenReturn(imageType);
    }

    @Test
    @Order(1)
    @DisplayName("Test the equals And HashCode Contract")
    public void equalsAndHashCodeContractTest() {

	EqualsVerifier.forClass(ImageConvertion.class) //
			.suppress(NONFINAL_FIELDS, STRICT_INHERITANCE, REFERENCE_EQUALITY) //
			.withIgnoredAnnotations(Entity.class, Id.class, Column.class, Table.class, GeneratedValue.class, ManyToOne.class, JoinColumn.class) //
			.withOnlyTheseFields("id") //
			.verify();

    }

    @Test
    @Order(2)
    @DisplayName("Test the toString method")
    public void toStringTest() {
	ToStringVerifier.forClass(ImageConvertion.class) //
			.withIgnoredFields("text", "created", "area") //
			.withClassName(SIMPLE_NAME) //
			.withFailOnExcludedFields(false) //
			.verify();
    }

    public Stream<Arguments> createValidImageConvertionData() throws IOException {

	return Stream.of( //
			Arguments.of(mockMultipartFile, WEB, false, null, null, null, null), //
			Arguments.of(mockMultipartFile, WS, true, 885, 1417, 1426, 57) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : executionType ''{1}'', area ''{2}'' ")
    @MethodSource("createValidImageConvertionData")
    @Order(3)
    @DisplayName("Test the imageConvertion's creation")
    public void createValidImageConvertionTest( //
		    final MultipartFile file, final ExecutionType executionType, //
		    final boolean area, final Integer x, final Integer y, final Integer width, final Integer height) {

	final var imageConvertionBuilder = new ImageConvertion.Builder().with($ -> {
	    $.file = file;
	    $.executionType = executionType;
	    $.x = x;
	    $.y = y;
	    $.width = width;
	    $.height = height;
	});

	final var imageConvertion = imageConvertionBuilder.build();

	assertThat(imageConvertion.getFileName()).isEqualToIgnoringCase(file.getOriginalFilename());

	assertThat(imageConvertion.getFileType().getExtension()).isEqualToIgnoringCase(getExtension(file.getOriginalFilename()));

	assertThat(imageConvertion.getFileSize()).isEqualTo(1878L);

	assertThat(imageConvertion.getType()).isEqualTo(executionType);

	assertThat(imageConvertion.getArea()).isEqualTo(area);
    }
}
