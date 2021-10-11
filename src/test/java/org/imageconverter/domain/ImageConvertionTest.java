package org.imageconverter.domain;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import static nl.jqno.equalsverifier.Warning.REFERENCE_EQUALITY;
import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.imageconverter.domain.imageConvertion.ExecutionType;
import org.imageconverter.domain.imageConvertion.ImageConvertion;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

@Tag("unit")
@DisplayName("Test the image type entity")
@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
public class ImageConvertionTest {

    private Validator validator;

    private ExecutableValidator executableValidator;

    @BeforeAll
    public void setUp() {
	validator = buildDefaultValidatorFactory().getValidator();

	executableValidator = buildDefaultValidatorFactory() //
			.getValidator() //
			.forExecutables();
    }

    @Test
    @Order(1)
    @DisplayName("Test the equals And HashCode Contract")
    public void equalsAndHashCodeContractTest() {

	EqualsVerifier.forClass(ImageConvertion.class) //
			.suppress(NONFINAL_FIELDS, STRICT_INHERITANCE, REFERENCE_EQUALITY) //
			.withIgnoredAnnotations(Entity.class, Id.class, Column.class, Table.class, GeneratedValue.class, ManyToOne.class, JoinColumn.class) //
			.withOnlyTheseFields("name", "type") //
			.verify();

    }

    @Test
    @Order(2)
    @DisplayName("Test the toString method")
    public void toStringTest() {
	ToStringVerifier.forClass(ImageConvertion.class) //
			.withIgnoredFields("text") //
			.withClassName(NameStyle.SIMPLE_NAME) //
//			.withOnlyTheseFields("")
			.withFailOnExcludedFields(false) //
			.verify();
    }

    public Stream<Arguments> createValidImageConvertionData() {

	return Stream.of( //
			Arguments.of(new CommonsMultipartFile(null)), //
			Arguments.of(ExecutionType.WEB) //
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
