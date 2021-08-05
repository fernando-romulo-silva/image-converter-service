package org.imageconverter.application;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.imageconverter.domain.ImageConvertion;
import org.imageconverter.domain.ImageConvertionRepository;
import org.imageconverter.domain.TesseractService;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.util.controllers.ImageConverterRequest;
import org.imageconverter.util.controllers.ImageConverterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageConversionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TesseractService tesseractService;

    private final ImageTypeService imageTypeService;

    private final ImageConvertionRepository repository;

    @Autowired
    public ImageConversionService(final TesseractService tesseractService, final ImageTypeService imageTypeService, final ImageConvertionRepository repository) {
	super();
	this.tesseractService = tesseractService;
	this.repository = repository;
	this.imageTypeService = imageTypeService;
    }

    @Transactional
    public ImageConverterResponse convert(@Valid final ImageConverterRequest request) {

	log.info("Starts with request {}.", request);

	final var file = request.data();

	if (Objects.isNull(file) || file.isEmpty()) {
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

	final var result = new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getName(), imageConvertion.getText());

	log.info("Ends with result {}.", result);

	return result;
    }

    @Transactional(readOnly = true)
    public List<ImageConverterResponse> getAll() {

	log.info("Starts getting all.");

	final var result = repository.findAll() //
			.stream() //
			.map(ic -> new ImageConverterResponse(ic.getId(), ic.getName(), ic.getText())) //
			.collect(toList()); //

	log.info("Ends with {} convertions.", result.size());

	return result;
    }
}
