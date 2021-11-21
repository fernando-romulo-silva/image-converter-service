package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.domain.imagetype.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ElementAlreadyExistsException;
import org.imageconverter.infra.exceptions.ElementConflictException;
import org.imageconverter.infra.exceptions.ElementInvalidException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imagetype.CreateImageTypeRequest;
import org.imageconverter.util.controllers.imagetype.ImageTypeResponse;
import org.imageconverter.util.controllers.imagetype.UpdateImageTypeRequest;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Loggable
public class ImageTypeService {

    public final ImageTypeRespository repository;

    @Autowired
    public ImageTypeService(final ImageTypeRespository repository) {
	super();
	this.repository = repository;
    }

    @Transactional
    public ImageTypeResponse createImageType(@NotNull @Valid final CreateImageTypeRequest request) {

	final var imageTypeOptional = repository.findByExtension(request.extension());

	if (imageTypeOptional.isPresent()) {
	    throw new ElementAlreadyExistsException(ImageType.class, "extension '" + request.extension() + "'");
	}

	final var imageType = new ImageType(request.extension(), request.name(), request.description());

	final var imageConvertion = repository.save(imageType);

	return new ImageTypeResponse(imageType.getId(), imageConvertion.getExtension(), imageType.getName());
    }

    @Transactional
    public ImageTypeResponse updateImageType(@NotNull final Long id, @NotNull @Valid final UpdateImageTypeRequest request) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageType.class, id));

	imageType.update(request.extension(), request.name(), request.description());

	final var imageTypeNew = repository.save(imageType);

	return new ImageTypeResponse(imageTypeNew.getId(), imageTypeNew.getExtension(), imageTypeNew.getName());
    }

    @Transactional(readOnly = true)
    public ImageTypeResponse findById(@NotNull final Long id) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageType.class, id));

	return new ImageTypeResponse(imageType.getId(), imageType.getExtension(), imageType.getName());
    }

    @Transactional(readOnly = true)
    public List<ImageTypeResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(imageType -> new ImageTypeResponse(imageType.getId(), imageType.getExtension(), imageType.getName())) //
			.toList();
    }

    @Transactional(readOnly = true)
    public List<ImageTypeResponse> findBySpecification(final Specification<ImageType> spec) {

	try {

	    return repository.findAll(spec) //
			    .stream() //
			    .map(imageType -> new ImageTypeResponse(imageType.getId(), imageType.getExtension(), imageType.getName())) //
			    .toList();

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final var invalidAttribute = substringBetween(msgException, "[", "]");

	    final var msg = format("Unable to locate Attribute with the the given name ''{0}'' on ImageType", invalidAttribute);

	    throw new ElementInvalidException(msg, ex);
	}
    }

    @Transactional
    public void deleteImageType(@NotNull final Long id) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageType.class, id));

	try {

	    repository.delete(imageType);

	    repository.flush();

	} catch (final DataIntegrityViolationException ex) {

	    throw new ElementConflictException(format("You cannot delete the image type {0} because it is already used", id.toString()), ex);
	}
    }

}
