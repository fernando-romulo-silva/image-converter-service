package org.imageconverter.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;

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

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("integration")
@DisplayName("Integration Test for ImageTypeService, happy path :D ")
public class ImageTypeServiceHappyPathTest {

    @Autowired
    private ImageTypeService imageTypeService;

    private CreateImageTypeRequest createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");

    @Test
    @Order(1)
    @DisplayName("Get a image type by id")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void getImageTypeByIdTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var result = imageTypeService.findById(id);

	assertThat(result.id()).isEqualTo(id);
    }

    @Test
    @Order(2)
    @DisplayName("Get all image types")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void getAllImageTypeTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var id = 1000L;

	final var responses = imageTypeService.findAll();

	assertThat(responses).map(f -> f.id()).contains(id);

	assertThat(responses).map(f -> f.extension()).containsAnyOf("png", "jpg"); // we have 'png' and 'jpg' at db-data-test.sql
    }

    static Specification<ImageType> equalsExtension(final String extension) {
	return (rt, cq, cb) -> cb.equal(rt.get("extension"), extension);
    }

    @Test
    @Order(3)
    @DisplayName("Get a image type by specification")
    public void getImageTypeBySpecificationTest() throws Exception {

	// already on db, due to the db-data-test.sql
	final var extension = "png";

	// Specification<ImageConvertion> spec = (rt, cq, cb) -> cb.equal(rt.get("extension"), extension);

	final var responses1 = imageTypeService.findBySpecification(equalsExtension(extension));

	assertThat(responses1).map(f -> f.extension()).containsAnyOf(extension);

	final var responses2 = imageTypeService.findBySpecification(null);

	assertThat(responses2).hasSize(2); // we have 'png' and 'jpg' at db-data-test.sql

	assertThat(responses2).map(f -> f.extension()).containsAnyOf("png", "jpg"); // we have 'png' and 'jpg' at db-data-test.sql
    }

    @Test
    @Order(4)
    @DisplayName("Create a new image type")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void createImageTypeTest() throws Exception {

	final var response = imageTypeService.createImageType(createImageTypeRequest);

	assertThat(response).isNotNull();

	assertThat(response.extension()).isEqualTo(createImageTypeRequest.extension());

	assertThat(response.name()).isEqualTo(createImageTypeRequest.name());
    }

    @Test
    @Order(5)
    @DisplayName("Update a image type")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void updateImageTypeTest() throws Exception {

	final var createResponse = imageTypeService.createImageType(createImageTypeRequest);

	final var newTypeRequest = new UpdateImageTypeRequest(null, "BitmapNew", null);

	final var updateResponse = imageTypeService.updateImageType(createResponse.id(), newTypeRequest);

	assertThat(updateResponse).isNotNull();

	assertThat(updateResponse.id()).isEqualTo(createResponse.id());

	assertThat(updateResponse.extension()).isEqualTo(createResponse.extension());

	assertThat(updateResponse.name()).isNotEqualTo(createResponse.name());

	assertThat(updateResponse.name()).isEqualTo(newTypeRequest.name());
    }

    @Test
    @Order(6)
    @DisplayName("Delete a new image type")
    @Sql(statements = "DELETE FROM image_type WHERE IMT_EXTENSION = 'BMP' ")
    public void deleteImageTypeTest() throws Exception {

	final var createResponse = imageTypeService.createImageType(createImageTypeRequest);

	final var findByIdResponse = imageTypeService.findById(createResponse.id());

	assertThat(findByIdResponse.id()).isEqualTo(createResponse.id());

	// ----------------------------------------------------------

	imageTypeService.deleteImageType(createResponse.id());

	// ----------------------------------------------------------

	assertThatThrownBy(() -> {

	    imageTypeService.findById(createResponse.id()); //

	}).isInstanceOfAny(ElementNotFoundException.class);
    }
}