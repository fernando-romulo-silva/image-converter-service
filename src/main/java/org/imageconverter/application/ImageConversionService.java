package org.imageconverter.application;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;
import static org.apache.commons.csv.QuoteMode.NONE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.imageconverter.domain.conversion.ImageConversion;
import org.imageconverter.domain.conversion.ImageConversionRepository;
import org.imageconverter.infra.exception.CsvFileGenerationException;
import org.imageconverter.infra.exception.CsvFileNoDataException;
import org.imageconverter.infra.exception.ElementAlreadyExistsException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementNotFoundException;
import org.imageconverter.infra.exception.ElementWithIdNotFoundException;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConversionResponse;
import org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.imageconverter.infra.util.logging.Loggable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    public static final String [] HEADER_FILE = { "id", "file-name", "result" };

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
     * @param request A image ({@link org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequest} or {@link org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestArea})
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

	repository.findByFileName(fileName).ifPresent(imageConversionResult -> {

	    throw new ElementAlreadyExistsException( //
			    ImageConversion.class, //
			    new Object[] { format("fileName ''{0}''", fileName) } //
	    );
	});

	final var imageConversion = repository.save(imageConversionNew);

	return new ImageConversionResponse(imageConversion.getId(), imageConversion.getFileName(), imageConversion.getText());
    }

    /**
     * Convert a list of image on text.
     * 
     * @param request A image ({@link org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequest} or {@link org.imageconverter.infra.util.controllers.imageconverter.ImageConverterRequestArea})
     *                that it'll be convert
     * @return A {@link ImageConversionResponse} with the conversion
     * @exception ElementAlreadyExistsException if image (file name) has already converted
     */
    @Transactional
    public List<ImageConversionResponse> convert(@NotNull @NotEmpty @Valid final List<ImageConverterRequestInterface> requests) {

	final var imageList = new ArrayList<ImageConversion>();

	for (final var request : requests) {
	    final var imageConversionNew = new ImageConversion.Builder() // NOPMD - AvoidInstantiatingObjectsInLoop: It's necessary to create a new object for each element
			    .with(request) //
			    .build();

	    repository.findByFileName(imageConversionNew.getFileName()).ifPresent(imageConversionResult -> {

		throw new ElementAlreadyExistsException( //
				ImageConversion.class, //
				createParams(imageConversionResult) //
		);
	    });

	    imageList.add(imageConversionNew);
	}

	final var imagesConversion = repository.saveAll(imageList);

	return imagesConversion //
			.stream() //
			.map(img -> new ImageConversionResponse(img.getId(), img.getFileName(), img.getText())) //
			.toList();

    }

    private Object[] createParams(final ImageConversion imageConversionResult) {
	return new Object[] { format("fileName '{0}', id '{1}'", imageConversionResult.getFileName(), imageConversionResult.getId()) };
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
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageConversion.class, id));

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
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageConversion.class, id));

	return new ImageConversionResponse(imageConversion.getId(), imageConversion.getFileName(), imageConversion.getText());
    }

    /**
     * Find image conversions by spring specification
     * 
     * @param spec The query specification, a {@link Specification} object
     * @return A {@link ImageConversionResponse}'s list with result or a empty list
     * @param pageable The query page control, a {@link Pageable} object
     * @exception ElementInvalidException if a specification is invalid
     */
    @Transactional(readOnly = true)
    public Page<ImageConversionResponse> findBySpecification(final Specification<ImageConversion> spec, final Pageable page) {

	try {

	    return repository.findAll(spec, page) //
			    .map(icr -> new ImageConversionResponse(icr.getId(), icr.getFileName(), icr.getText())); //

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final Object[] params = { substringBetween(msgException, "[", "]"), ImageConversion.class.getSimpleName() };

	    throw new ElementInvalidException("{exception.ElementInvalidDataSpecification}", ex, params);
	}
    }

    /**
     * Create a CSV file based on the result filtered by the spec
     * 
     * @param spec A {@link Specification} object, the query filter
     * @return An array of bytes (file in memory) 
     */
    @Transactional(readOnly = true)
    public byte[] findBySpecificationToCsv(final Specification<ImageConversion> spec) {

	final var pageable = Pageable.ofSize(1000);

	// TODO replace Page to Slice when it supports Specification
	var page = repository.findAll(spec, pageable);

	if (page.isEmpty()) {
	    throw new CsvFileNoDataException("{exception.csvEmpyResult}", spec);
	}

	final var csvFormat = CSVFormat.Builder.create() //
			.setHeader(HEADER_FILE) //
			.setAllowMissingColumnNames(true) //
			.setNullString(EMPTY) //
			.setQuoteMode(NONE) //
			.setDelimiter(';') //
			.setEscape(' ') //
			.build();

	try (final var byteArrayOutputStream = new ByteArrayOutputStream(); //
			final var outStreamWriter = new OutputStreamWriter(byteArrayOutputStream, UTF_8); //
			final var csvPrinter = new CSVPrinter(outStreamWriter, csvFormat);) {

	    do {

		var imageConversionList = page.getContent();

		for (final var imageConversion : imageConversionList) {
		    csvPrinter.printRecord(imageConversion.getId().toString(), imageConversion.getFileName(), imageConversion.getText());
		}

		csvPrinter.flush();

		page = repository.findAll(spec, page.nextOrLastPageable());
		imageConversionList = page.getContent();

	    } while (page.hasNext());

	    return byteArrayOutputStream.toByteArray();

	} catch (final IOException ex) {
	    throw new CsvFileGenerationException("{exception.csvUnexpectedError}", ex);
	}
    }
}
