package org.imageconverter.domain.imagetype;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.imageconverter.util.BeanUtil.defineContext;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
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
public class ImageTypeUnHappyPathTest {

    @Mock
    private ApplicationContext applicationContext;

    @BeforeAll
    public void setUp() {
	final var validator = buildDefaultValidatorFactory().getValidator();

	MockitoAnnotations.openMocks(this);

	defineContext(applicationContext);

	when(applicationContext.getBean(Validator.class)) //
			.thenReturn(validator);
    }

    public Stream<Arguments> createInvalidImageTypeData() throws IOException {

	return Stream.of( //
			Arguments.of(null, "PNG", "Portable Network Graphics"), //
			Arguments.of("", "PNG", "Portable Network Graphics"), //
			Arguments.of("png", null, "Portable Network Graphics"), //
			Arguments.of("png", "", "Portable Network Graphics")//
	);
    }

    @ParameterizedTest(name = "Pos {index} : name ''{0}'', extension ''{1}'', description ''{2}'' ")
    @MethodSource("createInvalidImageTypeData")
    @Order(1)
    @DisplayName("Test the imageTypes creation with invalid values")
    public void createInvalidImageTypeTest(final String name, final String extension, final String description) {

	assertThatThrownBy(() -> {

	    new ImageType(extension, name, description);

	}).isInstanceOf(RuntimeException.class);
    }

}
