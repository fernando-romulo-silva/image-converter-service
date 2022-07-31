package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ImageConvertion;
import org.imageconverter.domain.convertion.ImageConvertionRepository;
import org.imageconverter.infra.exceptions.ElementAlreadyExistsException;
import org.imageconverter.infra.exceptions.ElementInvalidException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;
import org.imageconverter.util.logging.Loggable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service that execute convertion.
 * 
 * @author Fernando Romulo da Silva
 */
@Service
@Loggable
public class ImageConversionService {

    private final ImageConvertionRepository repository;

    /**
     * Default constructor.
     * 
     * @param newRepository ImageCoversion repository
     */
    ImageConversionService(final ImageConvertionRepository newRepository) {
	super();
	this.repository = newRepository;
    }

    /**
     * Convert an image on text.
     * 
     * @param request A image ({@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequest} or {@link org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea})
     *                that it'll be convert
     * @return A {@link ImageConverterResponse} with the conversion
     * @exception ElementAlreadyExistsException if image (file name) has already converted
     */
    @Transactional
    public ImageConverterResponse convert(@NotNull @Valid final ImageConverterRequestInterface request) {

	final var imageConvertionNew = new ImageConvertion.Builder() //
			.with(request) //
			.build();

	final var fileName = imageConvertionNew.getFileName();

	repository.findByFileName(fileName).ifPresent(c -> {
	    throw new ElementAlreadyExistsException(ImageConvertion.class, "fileName '" + fileName + "' and with id '" + c.getId() + "'");
	});

	final var imageConvertion = repository.save(imageConvertionNew);

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getFileName(), imageConvertion.getText());
    }

    /**
     * Find all stored convertions or a empty list.
     * 
     * @return A list of {@link ImageConverterResponse} or a empty list
     */
    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(icr -> new ImageConverterResponse(icr.getId(), icr.getFileName(), icr.getText())) //
			.toList(); //
    }

    /**
     * Find a stored convertion by id.
     * 
     * @param id The image convertion's id
     * @return A {@link ImageConverterResponse} object
     * @exception ElementNotFoundException if a element with id not found
     */
    @Transactional(readOnly = true)
    public ImageConverterResponse findById(@NotNull final Long id) {

	final var imageConvertion = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageConvertion.class, id));

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getFileName(), imageConvertion.getText());
    }

    /**
     * Find image convertions by spring specification
     * 
     * @param spec The query specification, a {@link Specification} object
     * @return A {@link ImageConverterResponse}'s list with result or a empty list
     * @exception ElementInvalidException if a specification is invalid
     */
    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findBySpecification(final Specification<ImageConvertion> spec) {

	try {

	    return repository.findAll(spec) //
			    .stream() //
			    .map(icr -> new ImageConverterResponse(icr.getId(), icr.getFileName(), icr.getText())) //
			    .toList();

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final var invalidAttribute = substringBetween(msgException, "[", "]");

	    final var msg = format("Unable to locate Attribute with the the given name ''{0}'' on ImageConvertion", invalidAttribute);

	    throw new ElementInvalidException(msg, ex);
	}
    }
}
