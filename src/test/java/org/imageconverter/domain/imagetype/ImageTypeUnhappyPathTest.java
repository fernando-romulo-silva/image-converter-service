package org.imageconverter.domain.imagetype;

import static java.text.MessageFormat.format;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.infra.util.BeanUtil.defineContext;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@Tag("unit")
@DisplayName("Test the image type entity, unhappy path :( ")
@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class ImageTypeUnhappyPathTest {

	private static final String PORTABLE_NETWORK_GRAPHICS_TXT = "Portable Network Graphics";

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

	Stream<Arguments> createInvalidImageTypeData() {

		return Stream.of( //
				Arguments.of(null, "PNG", PORTABLE_NETWORK_GRAPHICS_TXT), //
				Arguments.of("", "PNG", PORTABLE_NETWORK_GRAPHICS_TXT), //
				Arguments.of("png", null, PORTABLE_NETWORK_GRAPHICS_TXT), //
				Arguments.of("png", "", PORTABLE_NETWORK_GRAPHICS_TXT)//
		);
	}

	@ParameterizedTest(name = "Pos {index} : name ''{0}'', extension ''{1}'', description ''{2}'' ")
	@MethodSource("createInvalidImageTypeData")
	@Order(1)
	@DisplayName("Test the imageTypes creation with invalid values")
	void createInvalidImageTypeTest(
			// given
			final String name, final String extension, final String description) {

		// when
		assertThatThrownBy(() -> {

			new ImageType(extension, name, description);

		}) // then
				.as(format("Check the invalid extension ''{0}'', name ''{1}'' and description ''{2}''", extension,
						name, description)) //
				.isInstanceOf(RuntimeException.class);
	}

}
