package org.imageconverter.application;

import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imageConvertion.ImageConvertion;
import org.imageconverter.domain.imageConvertion.ImageConvertionRepository;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.imageconverter.util.controllers.imageconverter.ImageConverterResponse;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Loggable
public class ImageConversionService {

    private final ImageConvertionRepository repository;

    private final Validator validator;

    @Autowired
    public ImageConversionService( //
		    final ImageTypeService imageTypeService, //
		    final ImageConvertionRepository repository, //
		    final Validator validator //
    ) {
	super();
	this.repository = repository;
	this.validator = validator;
    }

    @Transactional
    public ImageConverterResponse convert(@Valid final ImageConverterRequestInterface request) {

	final var violations = validator.validate(request);

	if (!violations.isEmpty()) {
	    throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

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
	return repository.findAll(spec) //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getFileName(), ic.getText())) //
			.toList();
    }
}
