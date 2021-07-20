package org.imageconverter.application;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.util.List;
import java.util.Objects;

import org.imageconverter.domain.ImageConvertion;
import org.imageconverter.domain.ImageConvertionRepository;
import org.imageconverter.domain.ImageType;
import org.imageconverter.domain.TesseractService;
import org.imageconverter.infra.ConvertionImageException;
import org.imageconverter.util.ImageConverterRequest;
import org.imageconverter.util.ImageConverterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageConversionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TesseractService tesseractService;

    private final ImageConvertionRepository repository;

    @Autowired
    public ImageConversionService(final TesseractService tesseractService, final ImageConvertionRepository repository) {
	super();
	this.tesseractService = tesseractService;
	this.repository = repository;
    }

    @Transactional
    public ImageConverterResponse convert(final ImageConverterRequest imageConverterRequest) {

	log.info("Starts image request {}.", imageConverterRequest);
	
	final var file = imageConverterRequest.data();
	
	if (Objects.isNull(file) || file.isEmpty()) {
	    throw new ConvertionImageException("Empty file to convert!");
	}

	final var extension = getExtension(file.getOriginalFilename());
	
	final var text = tesseractService.convert(imageConverterRequest);

	final var imageConvertionNewBuilder = new ImageConvertion.Builder().with($ -> {
	    $.executionType = imageConverterRequest.executionType();
	    $.name = imageConverterRequest.data().getName() + "." + extension;
	    $.size = imageConverterRequest.data().getSize() / 1024;
	    $.type = ImageType.from(extension);
	    $.text = text;
	});

	final var imageConvertion = repository.save(imageConvertionNewBuilder.build());

	final var result = new ImageConverterResponse(imageConvertion.getId(), imageConvertion.getName(), imageConvertion.getText());

	log.info("Ends result {}.", result);

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
