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
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param repository ImageCoversion repository
     */
    @Autowired
    ImageConversionService(final ImageConvertionRepository repository) {
	super();
	this.repository = repository;
    }

    /**
     * Convert an image on text.
     * 
     * @param request A image that it'll be convert
     * @return A imageConverterResponse with the conversion
     * @see ImageConverterResponse
     * @exception ElementAlreadyExistsException if image, file name, has already converted
     */
    @Transactional
    public ImageConverterResponse convert(@NotNull @Valid final ImageConverterRequestInterface request) {

	final var imageConvertionNew = new ImageConvertion.Builder() //
			.with(request).build();

	final var fileName = imageConvertionNew.getFileName();

	final var imageConvertionOptional = repository.findByFileName(fileName);

	if (imageConvertionOptional.isPresent()) {
	    throw new ElementAlreadyExistsException(ImageConvertion.class, "fileName '" + fileName + "' and with text '" + imageConvertionOptional.get().getText() + "'");
	}

	final var imageConvertion = repository.save(imageConvertionNew);

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getFileName(), imageConvertion.getText());
    }

    /**
     * Find all stored convertions.
     * 
     * @return A list of ImageConverterResponse or a empty list
     * @see ImageConverterResponse
     */
    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getFileName(), ic.getText())) //
			.toList(); //
    }

    /**
     * Find a stored convertion by id.
     * 
     * @param id The image convertion's id
     * @return A list of ImageConverterResponse
     * @see ImageConverterResponse
     * @exception ElementNotFoundException if image, file name, has already converted
     */
    @Transactional(readOnly = true)
    public ImageConverterResponse findById(@NotNull final Long id) {

	final var imageConvertion = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageConvertion.class, id));

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getFileName(), imageConvertion.getText());
    }

    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findBySpecification(final Specification<ImageConvertion> spec) {

	try {

	    return repository.findAll(spec) //
			    .stream() //
			    .map(ic -> new ImageConverterResponse(ic.getId(), ic.getFileName(), ic.getText())) //
			    .toList();

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final var invalidAttribute = substringBetween(msgException, "[", "]");

	    final var msg = format("Unable to locate Attribute with the the given name ''{0}'' on ImageConvertion", invalidAttribute);

	    throw new ElementInvalidException(msg, ex);
	}
    }
}
