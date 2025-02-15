package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.io.IOException;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.infra.exception.ElementAlreadyExistsException;
import org.imageconverter.infra.exception.ElementConflictException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.util.controllers.imagetype.ImageTypeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * Test the {@link ImageTypeService} on unhappy path
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("integration")
@DisplayName("Integration Test for ImageTypeService, unhappy path :( 𝅘𝅥𝅮  Hello, darkness, my old friend ")
@TestInstance(PER_CLASS)
class ImageTypeServiceUnhappyPathTest {

    @Autowired
    private ImageTypeService imageTypeService;

    private final Pageable pageable = PageRequest.of(0, 10);

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 1L) // id '1' don't exist
    @Order(1)
    @DisplayName("Search a image type that doesn't exist by id")
    void findImageTypeByIdTest(final Long id) {

        assertThatThrownBy(() -> imageTypeService.findById(id)) //
                .as(format("Check invalid values of id ''{0}''", id)) //
                .isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);

    }

    @Test
    @Order(2)
    @DisplayName("Get a image type by invalid specification")
    void findImageTypeByInvalidExtensionTest() {

        final var specFieldOneNotExists = (Specification<ImageType>) (root, query, builder) -> builder
                .equal(root.get("fieldOneNotExists"), "blabla");
        final var specFieldTwoNotExists = (Specification<ImageType>) (root, query, builder) -> builder
                .equal(root.get("fieldTwoNotExists"), "blabla");

        assertThatThrownBy(
                () -> imageTypeService.findBySpecification(specFieldOneNotExists.and(specFieldTwoNotExists), pageable))
                .as(format("Check invalid Specification")) //
                .isInstanceOf(ElementInvalidException.class);
    }

    Stream<Arguments> createImageTypeInvalidData() throws IOException {

        final ImageTypeRequest nullCreateImageTypeRequest = null;

        return Stream.of( //
                Arguments.of(new ImageTypeRequest(null, "BitMap", "Device independent bitmap")), //
                Arguments.of(new ImageTypeRequest("BMP", null, "Device independent bitmap")), //
                Arguments.of(nullCreateImageTypeRequest) //
        );
    }

    @ParameterizedTest(name = "Pos {index} : request ''{0}'' ")
    @MethodSource("createImageTypeInvalidData")
    @Order(3)
    @DisplayName("Try to create a invalid image type")
    void createImageTypeInvalidTest(final ImageTypeRequest request) {

        assertThatThrownBy(() -> imageTypeService.createImageType(request))
                .as(format("Check invalid request ''{0}''", request)) //
                .isInstanceOfAny(ConstraintViolationException.class);

    }

    @Test
    @Order(4)
    @DisplayName("Create twice the same image type")
    void createSameImageTypeTest() {

        final var createImageTypeRequest = new ImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

        final var createImageTypeRequestAgain = new ImageTypeRequest("BMP", "Bla", "Bla bla bla bla");

        imageTypeService.createImageType(createImageTypeRequest);

        //
        assertThatThrownBy(() -> imageTypeService.createImageType(createImageTypeRequestAgain))
                .as(format("Check if throw an exception on same request ''{0}''", createImageTypeRequest)) //
                .isInstanceOfAny(ElementAlreadyExistsException.class);
    }

    Stream<Arguments> updateImageTypeDoesNotExistData() throws IOException {

        final ImageTypeRequest nullUpdateImageTypeRequest = null;

        return Stream.of( //
                Arguments.of(12_345L, new ImageTypeRequest(null, "BitmapNew", null)), //
                Arguments.of(null, new ImageTypeRequest(null, "BitmapNew", null)), //
                Arguments.of(1000L, nullUpdateImageTypeRequest) //
        );
    }

    @ParameterizedTest(name = "Pos {index} : id ''{0}'', request ''{1}'' ")
    @MethodSource("updateImageTypeDoesNotExistData")
    @Order(5)
    @DisplayName("Try to update a image type that doesn't exist")
    void updateImageTypeDoesNotExistTest(final Long id, final ImageTypeRequest request) {

        assertThatThrownBy(() -> imageTypeService.updateImageType(id, request))
                .as(format("Check if throw a exception on invalid request ''{0}''", request)) //
                .isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 1L) // id '1' don't exist
    @Order(6)
    @DisplayName("Try to delete a image type that doesn't exist")
    void deleteImageTypeDoesNotExistTest(final Long id) {

        assertThatThrownBy(() -> imageTypeService.deleteImageType(id))
                .as(format("Check if throw a exception on invalid delete, id ''{0}''", id)) //
                .isInstanceOfAny(ConstraintViolationException.class, ElementNotFoundException.class);
    }

    @Test
    @Order(7)
    @DisplayName("Try to delete a image type that has a relation with other record")
    void deleteImageTypeRestrictionTest() {

        final var id = 1001L; // already exists and has a conversion image relation

        assertThatThrownBy(() -> imageTypeService.deleteImageType(id))
                .as(format("Check if throw a exception on invalid delete, id ''{0}''", id)) //
                .isInstanceOfAny(ElementConflictException.class);
    }

}
