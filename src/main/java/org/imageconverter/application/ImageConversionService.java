package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.conversion.ImageConversion;
import org.imageconverter.domain.conversion.ImageConversionRepository;
import org.imageconverter.infra.exception.ElementAlreadyExistsException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.imageconverter.util.controllers.imageconverter.ImageConversionResponse;
import org.imageconverter.util.logging.Loggable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that execute conversion.
 * 
 * @author Fernando Romulo da Silva
 */
@Service
@Loggable
public class ImageConversionService {

    private final ImageConversionRepository repository;

    /**
     * Default constructor.
     * 
     * @param newRepository ImageCoversion repository
     */
    ImageConversionService(final ImageConversionRepository newRepository) {
	super();
	this.repository = newRepository;
    }

    /**
     * Convert an image on text.
     * 
     * @param request A image ({@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequest} or {@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea})
     *                that it'll be convert
     * @return A {@link ImageConversionResponse} with the conversion
     * @exception ElementAlreadyExistsException if image (file name) has already converted
     */
    @Transactional
    public ImageConversionResponse convert(@NotNull @Valid final ImageConverterRequestInterface request) {

	final var imageConversionNew = new ImageConversion.Builder() //
			.with(request) //
			.build();

	final var fileName = imageConversionNew.getFileName();

	repository.findByFileName(fileName).ifPresent(c -> {
	    throw new ElementAlreadyExistsException(ImageConversion.class, "fileName '" + fileName + "' and with id '" + c.getId() + "'");
	});

	final var imageConversion = repository.save(imageConversionNew);

	return new ImageConversionResponse(imageConversion.getId(), imageConversion.getFileName(), imageConversion.getText());
    }

    /**
     * Convert a list of image on text.
     * 
     * @param request A image ({@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequest} or {@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea})
     *                that it'll be convert
     * @return A {@link ImageConversionResponse} with the conversion
     * @exception ElementAlreadyExistsException if image (file name) has already converted
     */
    @Transactional
    public List<ImageConversionResponse> convert(@NotNull @NotEmpty @Valid final List<ImageConverterRequestInterface> requests) {

	final var imageList = new ArrayList<ImageConversion>();

	for (final var request : requests) {
	    final var imageConversionNew = new ImageConversion.Builder() //
			    .with(request) //
			    .build();

	    final var fileName = imageConversionNew.getFileName();

	    repository.findByFileName(fileName).ifPresent(c -> {
		throw new ElementAlreadyExistsException(ImageConversion.class, "fileName '" + fileName + "' and with id '" + c.getId() + "'");
	    });

	    imageList.add(imageConversionNew);
	}

	final var imagesConversion = repository.saveAll(imageList);

	return imagesConversion //
			.stream() //
			.map(img -> new ImageConversionResponse(img.getId(), img.getFileName(), img.getText())) //
			.toList();

    }

    /**
     * Delete a conversion image.
     * 
     * @param id The image type's id
     * @exception ElementNotFoundException if conversion (id) doesn't exists
     */
    @Transactional
    public void deleteImageConversion(@NotNull final Long id) {

	final var imageConversion = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageConversion.class, id));

	repository.delete(imageConversion);

	repository.flush();
    }

    /**
     * Find all stored conversions or a empty list.
     * 
     * @return A list of {@link ImageConversionResponse} or a empty list
     */
    @Transactional(readOnly = true)
    public List<ImageConversionResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(icr -> new ImageConversionResponse(icr.getId(), icr.getFileName(), icr.getText())) //
			.toList(); //
    }

    /**
     * Find a stored conversion by id.
     * 
     * @param id The image conversion's id
     * @return A {@link ImageConversionResponse} object
     * @exception ElementNotFoundException if a element with id not found
     */
    @Transactional(readOnly = true)
    public ImageConversionResponse findById(@NotNull final Long id) {

	final var imageConversion = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageConversion.class, id));

	return new ImageConversionResponse(imageConversion.getId(), imageConversion.getFileName(), imageConversion.getText());
    }

    /**
     * Find image conversions by spring specification
     * 
     * @param spec The query specification, a {@link Specification} object
     * @return A {@link ImageConversionResponse}'s list with result or a empty list
     * @exception ElementInvalidException if a specification is invalid
     */
    @Transactional(readOnly = true)
    public List<ImageConversionResponse> findBySpecification(final Specification<ImageConversion> spec) {

	try {

	    return repository.findAll(spec) //
			    .stream() //
			    .map(icr -> new ImageConversionResponse(icr.getId(), icr.getFileName(), icr.getText())) //
			    .toList();

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final var invalidAttribute = substringBetween(msgException, "[", "]");

	    final var msg = format("Unable to locate Attribute with the the given name ''{0}'' on ImageConversion", invalidAttribute);

	    throw new ElementInvalidException(msg, ex);
	}
    }
}
