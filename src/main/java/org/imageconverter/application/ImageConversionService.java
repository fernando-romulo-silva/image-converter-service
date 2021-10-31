package org.imageconverter.application;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.convertion.ImageConvertion;
import org.imageconverter.domain.convertion.ImageConvertionRepository;
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

@Service
@Loggable
public class ImageConversionService {

    private final ImageConvertionRepository repository;

    @Autowired
    public ImageConversionService(final ImageConvertionRepository repository) {
	super();
	this.repository = repository;
    }

    @Transactional
    public ImageConverterResponse convert(@NotNull @Valid final ImageConverterRequestInterface request) {

	final var imageConvertionNew = new ImageConvertion.Builder() //
			.with(request).build();

	final var imageConvertion = repository.save(imageConvertionNew);

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getFileName(), imageConvertion.getText());
    }

    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getFileName(), ic.getText())) //
			.toList(); //
    }

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

	    throw new ElementInvalidException(msg);
	}

    }
}
