package org.imageconverter.domain.imagetype;

import static com.jparams.verifier.tostring.NameStyle.SIMPLE_NAME;
import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static nl.jqno.equalsverifier.Warning.REFERENCE_EQUALITY;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.util.BeanUtil.defineContext;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Validator;

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

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Test the {@link ImageType} class on happy path.
 * 
 * @author Fernando Romulo da Silva
 */
@Tag("unit")
@DisplayName("Test the image type entity, happy Path :) ")
@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
class ImageTypeHappyPathTest {

    @Mock
    private ApplicationContext applicationContext;

    @BeforeAll
    void setUp() {
	final var validator = buildDefaultValidatorFactory().getValidator();

	MockitoAnnotations.openMocks(this);

	defineContext(applicationContext);

	when(applicationContext.getBean(Validator.class)) //
			.thenReturn(validator);
    }

    @Test
    @Order(1)
    @DisplayName("Test the equals And HashCode Contract")
    void tryEqualsAndHashCodeContractTest() { // NOPMD - JUnitTestsShouldIncludeAssert: EqualsVerifier already do it

	EqualsVerifier.forClass(ImageType.class) //
			.suppress(NONFINAL_FIELDS, STRICT_INHERITANCE, REFERENCE_EQUALITY) //
			.withIgnoredAnnotations(Entity.class, Id.class, Column.class, Table.class, GeneratedValue.class, ManyToOne.class, JoinColumn.class) //
			.withOnlyTheseFields("id") //
			.verify();

    }

    @Test
    @Order(2)
    @DisplayName("Test the toString method")
    void tryToStringTest() { // NOPMD - JUnitTestsShouldIncludeAssert: ToStringVerifier already do it
	ToStringVerifier.forClass(ImageType.class) //
			.withIgnoredFields("description", "created", "updated") //
			.withClassName(SIMPLE_NAME) //
			.withFailOnExcludedFields(false) //
			.verify();
    }

    Stream<Arguments> createValidImageTypeData() throws IOException {

	return Stream.of( //
			Arguments.of("png", "PNG", "Portable Network Graphics"), //
			Arguments.of("bmp", "BitMap", null) //
	);
    }

    @ParameterizedTest(name = "Pos {index} : name ''{0}'', extension ''{1}'', description ''{2}'' ")
    @MethodSource("createValidImageTypeData")
    @Order(3)
    @DisplayName("Test the imageTypes creation")
    void createValidImageTypeTest(final String name, final String extension, final String description) {

	final var imageType = new ImageType(extension, name, description);

	assertThat(imageType) //
			.as(format("Check the name ''{0}'', extension ''{1}'' and description ''{3}''", name, extension, description)) //
			.extracting("name", "extension", "description") //
			.containsExactly(name, extension, ofNullable(description)) //
	;

	final var now = LocalDateTime.now();

	assertThat(imageType.getCreated()) //
			.as(format("Check the dt created is before or equal to ''{0}''", now)) //
			.isBeforeOrEqualTo(now);
    }

    @Test
    @Order(4)
    @DisplayName("Test the imageTypes update")
    void updateValidImageTypeTest() {

	final var extension = "png";
	final var name = "PNG";
	final var description = "Portable Network Graphics";

	final var imageType = new ImageType(extension, name, description);

	final var newExtension = "png_new";

	final var newName = "PNG_NEW";

	final var newDescription = "new Description";

	final LocalDateTime localDateTime = null;

	assertThat(imageType) //
			.as(format("Check if name ''{0}'' is not changed and updated is empty", imageType.getName())) //
			.extracting("updated", "name") //
			.containsExactly(ofNullable(localDateTime), name) //
	;

	imageType.update(null, newName, null);

	imageType.update(newExtension, null, null);

	imageType.update(null, null, newDescription);

	assertThat(imageType) //
			.as(format("Check if updated name ''{0}'' ", imageType.getName())) //
			.extracting("name", "extension", "description") //
			.containsExactly(newName, newExtension, Optional.of(newDescription)) //
	;

	assertThat(imageType.getUpdated().get()) //
			.as("Check if changed is updated") //
			.isBeforeOrEqualTo(LocalDateTime.now()) //
	;

    }
}
