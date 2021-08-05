package org.imageconverter.application;

import javax.validation.Valid;

import org.imageconverter.domain.ImageType;
import org.imageconverter.domain.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ImageTypeNotFoundException;
import org.imageconverter.util.controllers.ImageTypeRequest;
import org.imageconverter.util.controllers.ImageTypeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageTypeService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final ImageTypeRespository repository;

    @Autowired
    public ImageTypeService(final ImageTypeRespository repository) {
	super();
	this.repository = repository;
    }

    @Transactional
    public ImageTypeResponse createImageType(@Valid final ImageTypeRequest request) {
	log.info("Starts with request {}.", request);

	final var imageType = new ImageType(request.extension(), request.name());

	final var imageConvertion = repository.save(imageType);

	final var result = new ImageTypeResponse(imageType.getId(), imageConvertion.getExtension(), imageType.getName());

	log.info("Ends with result {}.", result);

	return result;
    }

    @Transactional(readOnly = true)
    public ImageType findImageType(final String extension) {
	log.info("Starts with extension {}.", extension);

	final var result = repository.findByExtension(extension) //
			.orElseThrow(() -> new ImageTypeNotFoundException(extension));

	log.info("Ends with result {}.", result);

	return result;
    }
}
