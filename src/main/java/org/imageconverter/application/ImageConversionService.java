package org.imageconverter.application;

import static java.util.Objects.isNull;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imageConvertion.ImageConvertion;
import org.imageconverter.domain.imageConvertion.ImageConvertionRepository;
import org.imageconverter.domain.imageConvertion.TesseractService;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.ImageConverterRequestInterface;
import org.imageconverter.util.controllers.ImageConverterResponse;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Loggable
public class ImageConversionService {

    private final TesseractService tesseractService;

    private final ImageTypeService imageTypeService;

    private final ImageConvertionRepository repository;

    private final Validator validator;

    @Autowired
    public ImageConversionService( //
		    final TesseractService tesseractService, //
		    final ImageTypeService imageTypeService, //
		    final ImageConvertionRepository repository, //
		    final Validator validator //
    ) {
	super();
	this.tesseractService = tesseractService;
	this.repository = repository;
	this.imageTypeService = imageTypeService;
	this.validator = validator;
    }

    @Transactional
    public ImageConverterResponse convert(@Valid final ImageConverterRequestInterface request) {

	final var violations = validator.validate(request);

	if (!violations.isEmpty()) {
	    throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

	final var file = request.data();

	if (isNull(file) || file.isEmpty()) {
	    throw new ConvertionException("Empty file to convert!");
	}

	final var extensionTxt = getExtension(file.getOriginalFilename());

	final var extension = imageTypeService.findImageType(extensionTxt);

	final var text = tesseractService.convert(request);

	final var imageConvertionNewBuilder = new ImageConvertion.Builder().with($ -> {
	    $.executionType = request.executionType();
	    $.name = request.data().getName() + "." + extensionTxt;
	    $.size = request.data().getSize() / 1024;
	    $.type = extension;
	    $.text = text;
	});

	final var imageConvertion = repository.save(imageConvertionNewBuilder.build());

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getName(), imageConvertion.getText());
    }

    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findAll() {

	final var result = repository.findAll() //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getName(), ic.getText())) //
			.toList(); //

	return result;
    }

    @Transactional(readOnly = true)
    public ImageConverterResponse findById(@NotNull final Long id) {

	final var imageConvertion = repository.findById(id) //
			.orElseThrow(() -> new ElementNotFoundException(ImageConvertion.class, id));

	return new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getName(), imageConvertion.getText());
    }

    @Transactional(readOnly = true)
    public List<ImageConverterResponse> findBySpecification(final Specification<ImageConvertion> spec) {
	return repository.findAll(spec) //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getName(), ic.getText())) //
			.toList();
    }
}
