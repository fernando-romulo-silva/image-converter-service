package org.imageconverter.application;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.ImageType;
import org.imageconverter.domain.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ImageTypeNotFoundException;
import org.imageconverter.util.controllers.CreateImageTypeRequest;
import org.imageconverter.util.controllers.ImageTypeResponse;
import org.imageconverter.util.controllers.UpdateImageTypeRequest;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ImageTypeResponse createImageType(@Valid final CreateImageTypeRequest request) {

	final var imageType = new ImageType(request.extension(), request.name(), request.description());

	final var imageConvertion = repository.save(imageType);

	final var result = new ImageTypeResponse(imageType.getId(), imageConvertion.getExtension(), imageType.getName());

	return result;
    }

    @Transactional
    public ImageTypeResponse updateImageType(@NotNull final Long id, @Valid final UpdateImageTypeRequest request) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ImageTypeNotFoundException(id));

	imageType.update(request.extension(), request.name(), request.description());

	final var imageConvertion = repository.save(imageType);

	return new ImageTypeResponse(imageType.getId(), imageConvertion.getExtension(), imageType.getName());
    }

    @Transactional(readOnly = true)
    public ImageType findImageType(@NotBlank final String extension) {

	final var result = repository.findByExtension(extension) //
			.orElseThrow(() -> new ImageTypeNotFoundException(extension));

	return result;
    }
}
