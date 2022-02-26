package org.imageconverter.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

import java.util.List;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imagetype.CreateImageTypeRequest;
import org.imageconverter.util.controllers.imagetype.UpdateImageTypeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * Test the {@link ImageTypeService} on happy path
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
@Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
//
@Tag("integration")
@DisplayName("Integration Test for ImageTypeService, happy path :D ")
class ImageTypeServiceHappyPathTest {

    private final ImageTypeService imageTypeService;

    private final CreateImageTypeRequest createImageTypeRequest;

    ImageTypeServiceHappyPathTest(@Autowired final ImageTypeService imageTypeService) {
	super();
	this.imageTypeService = imageTypeService;
	this.createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");
    }

    @Test
    @Order(1)
    @DisplayName("Get a image type by id")
    void findImageTypeByIdTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var result = imageTypeService.findById(id);

	assertThat(result.id()) //
			.as("Check response id is equal to '" + id + "'") //
			.isEqualTo(id);
    }

    @Test
    @Order(2)
    @DisplayName("Get all image types")
    void findAllImageTypeTest() {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var responses = imageTypeService.findAll();

	assertThat(responses) //
			.as("Responses contains id '" + id + "'") //
			.map(imageTypeResponse -> imageTypeResponse.id()) //
			.contains(id);

	assertThat(responses) //
			.as("Responses extensions contains 'png' and 'jpg'") //
			.map(imageTypeResponse -> imageTypeResponse.extension()) //
			.containsAll(List.of("png", "jpg")); // we have 'png' and 'jpg' at db-data-test.sql
    }

    static Specification<ImageType> equalsExtension(final String extension) {
	return (root, query, builder) -> builder.equal(root.get("extension"), extension);
    }

    @Test
    @Order(3)
    @DisplayName("Get a image type by specification")
    void findImageTypeBySpecificationTest() {

	// already on db, due to the db-data-test.sql
	final var extension = "png";

	final var responses1 = imageTypeService.findBySpecification(equalsExtension(extension));

	assertThat(responses1) //
			.as("Response1 contains the extension '" + extension + "'") //
			.map(imageTypeResponse -> imageTypeResponse.extension()) //
			.containsAnyOf(extension);

	// -----------------------

	final var responses2 = imageTypeService.findBySpecification(null);

	assertThat(responses2) //
			.as("Response2 contains two elements and its exensions are 'png' and 'jpg'") //
			.hasSize(2) //
			.map(imageTypeResponse -> imageTypeResponse.extension()) //
			.containsAll(List.of("png", "jpg")); // we have 'png' and 'jpg' at db-data-test.sql
    }

    @Test
    @Order(4)
    @DisplayName("Create a new image type")
    void createImageTypeTest() {

	final var response = imageTypeService.createImageType(createImageTypeRequest);

	assertThat(response) //
			.as("Response is not null and has extension '" + createImageTypeRequest.extension() + "' and name '" + createImageTypeRequest.name() + "'") //
			.isNotNull() //
			.hasFieldOrPropertyWithValue("extension", createImageTypeRequest.extension()) //
			.hasFieldOrPropertyWithValue("name", createImageTypeRequest.name()) //
	;
    }

    @Test
    @Order(5)
    @DisplayName("Update a image type")
    void updateImageTypeTest() {

	final var createResponse = imageTypeService.createImageType(createImageTypeRequest);

	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	final var updateResponse = imageTypeService.updateImageType(createResponse.id(), newTypeRequest);

	assertThat(updateResponse) //
			.as("Update response not changed id '" + createResponse.id() + "' and extension '" + createResponse.extension() + "'") //
			.isNotNull() //
			.hasFieldOrPropertyWithValue("id", createResponse.id()) //
			.hasFieldOrPropertyWithValue("extension", createResponse.extension()); //

	assertThat(updateResponse.name()) //
			.as("Update response changed name '" + updateResponse.name() + "'") //
			.isNotEmpty() //
			.isNotEqualTo(createResponse.name()).isEqualTo(newTypeRequest.name());
    }

    @Test
    @Order(6)
    @DisplayName("Delete a new image type")
    void deleteImageTypeTest() {

	final var createResponse = imageTypeService.createImageType(createImageTypeRequest);

	final var findByIdResponse = imageTypeService.findById(createResponse.id());

	assertThat(findByIdResponse.id()) //
			.as("Check the imageType was created").isEqualTo(createResponse.id());

	// ----------------------------------------------------------

	imageTypeService.deleteImageType(createResponse.id());

	// ----------------------------------------------------------

	assertThatThrownBy(() -> {

	    imageTypeService.findById(createResponse.id()); //

	}) //
			.as("Check the imageType was deleted") //
			.isInstanceOfAny(ElementNotFoundException.class);
    }
}
