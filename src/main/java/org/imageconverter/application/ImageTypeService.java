package org.imageconverter.application;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.imageconverter.infra.util.controllers.jsonpatch.JsonPatchOperation.REPLACE;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.domain.imagetype.ImageTypeRespository;
import org.imageconverter.infra.exception.ElementAlreadyExistsException;
import org.imageconverter.infra.exception.ElementConflictException;
import org.imageconverter.infra.exception.ElementInvalidException;
import org.imageconverter.infra.exception.ElementWithIdNotFoundException;
import org.imageconverter.infra.util.controllers.imagetype.ImageTypeRequest;
import org.imageconverter.infra.util.controllers.imagetype.ImageTypeResponse;
import org.imageconverter.infra.util.controllers.jsonpatch.JsonPatch;
import org.imageconverter.infra.util.logging.Loggable;
import org.springframework.boot.logging.LogLevel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

/**
 * Application service that manages (CRUD) image types.
 * 
 * @author Fernando Romulo da Silva
 */
@Service
@Loggable(value = LogLevel.INFO)
public class ImageTypeService {

    private final ImageTypeRespository repository;
    
    /**
     * Default constructor.
     * 
     * @param newRepository imageType repository
     */
    ImageTypeService(final ImageTypeRespository newRepository) {
	super();
	this.repository = newRepository;
    }

    /**
     * Create a image type.
     * 
     * @param request A image type ({@link ImageTypeRequest}) request to create
     * @return A {@link ImageTypeResponse} with the conversion
     * @exception ElementAlreadyExistsException if image type (file extension) has already exists
     */
    @Transactional
    public ImageTypeResponse createImageType(@NotNull @Valid final ImageTypeRequest request) {

	final var imageTypeOptional = repository.findByExtension(request.extension());

	if (imageTypeOptional.isPresent()) {
	    final Object[] params = { "extension '" + request.extension() + "'" };

	    throw new ElementAlreadyExistsException(ImageType.class, params);
	}

	final var imageType = new ImageType(request.extension(), request.name(), request.description());

	final var imageConversion = repository.save(imageType);

	return new ImageTypeResponse(imageType.getId(), imageConversion.getExtension(), imageType.getName());
    }
    
    /**
     * Update a whole image type.
     * 
     * @param id      The image type's id
     * @param request A image type ({@link ImageTypeRequest}) requested to update
     * @return A {@link ImageTypeResponse} with the update's result
     * @exception ElementNotFoundException if image type (file extension) doesn't exists
     */
    @Transactional
    public ImageTypeResponse updateImageType(@NotNull final Long id, @NotNull @Valid final ImageTypeRequest request) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageType.class, id));

	imageType.update(request.extension(), request.name(), request.description());

	final var imageTypeNew = repository.save(imageType);

	return new ImageTypeResponse(imageTypeNew.getId(), imageTypeNew.getExtension(), imageTypeNew.getName());
    }
    
    /**
     * Partially update a image type.
     * 
     * @param id      The image type's id
     * @param jsonPatchs A image type ({@link UpdateImageTypeRequest}) requested to update
     * @return A {@link ImageTypeResponse} with the update's result
     * @exception ElementNotFoundException if image type (file extension) doesn't exists
     */
    @Transactional
    public ImageTypeResponse updateImageType(final Long id, final List<JsonPatch> jsonPatchs) {
	
	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageType.class, id));
	
	final var imageTypePatched = applyPatchToJObject(jsonPatchs, imageType);
	
	final var imageTypeNew = repository.save(imageTypePatched);
	
	return new ImageTypeResponse(imageTypeNew.getId(), imageTypeNew.getExtension(), imageTypeNew.getName());
    }
    
    private ImageType applyPatchToJObject(final List<JsonPatch> jsonPatchs, final ImageType targetObject) {

	final var toReplace = jsonPatchs.stream() //
			.filter(jsonPatch -> jsonPatch.op().equals(REPLACE)) //
			.collect(toMap(JsonPatch::path, JsonPatch::value)); //

	toReplace.forEach((key, value) -> {
	    final var field = FieldUtils.getField(ImageType.class, key, true);
	    ReflectionUtils.setField(field, targetObject, value);
	});

	return targetObject;
    }    
    

    /**
     * Delete a image type.
     * 
     * @param id The image type's id
     * @exception ElementNotFoundException if image type (file extension) doesn't exists
     * @exception ElementConflictException if amage type already been used on image conversion
     */
    @Transactional
    public void deleteImageType(@NotNull final Long id) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageType.class, id));

	try {

	    repository.delete(imageType);

	    repository.flush();

	} catch (final DataIntegrityViolationException ex) {

	    throw new ElementConflictException("{exception.imageTypeDeleteDataIntegrityViolation}", ex, id.toString());
	}
    }

    /**
     * Find a image type by id.
     * 
     * @param id The image type's id
     * @return A {@link ImageTypeResponse} object
     * @exception ElementNotFoundException if a element with id not found
     */
    @Transactional(readOnly = true)
    public ImageTypeResponse findById(@NotNull final Long id) {

	final var imageType = repository.findById(id) //
			.orElseThrow(() -> new ElementWithIdNotFoundException(ImageType.class, id));

	return new ImageTypeResponse(imageType.getId(), imageType.getExtension(), imageType.getName());
    }

    /**
     * Find all stored image types or a empty list.
     * 
     * @return A list of {@link ImageTypeResponse} or a empty list
     */
    @Transactional(readOnly = true)
    public List<ImageTypeResponse> findAll() {

	return repository.findAll() //
			.stream() //
			.map(imageType -> new ImageTypeResponse(imageType.getId(), imageType.getExtension(), imageType.getName())) //
			.toList();
    }

    /**
     * Find image types by spring specification.
     * 
     * @param spec     The query specification, a {@link Specification} object
     * @param pageable The query page control, a {@link Pageable} object
     * @return A {@link ImageTypeResponse}'s list with result or a empty list
     * @exception ElementInvalidException if a specification is invalid
     */
    @Transactional(readOnly = true)
    public Page<ImageTypeResponse> findBySpecification(final Specification<ImageType> spec, final Pageable pageable) {

	try {

	    return repository.findAll(spec, pageable) //
			    .map(imageType -> new ImageTypeResponse( //
					    imageType.getId(), //
					    imageType.getExtension(), //
					    imageType.getName() //
			    ));

	} catch (final InvalidDataAccessApiUsageException ex) {

	    final var msgException = getRootCauseMessage(ex);

	    final Object[] params = { substringBetween(msgException, "[", "]"), ImageType.class.getSimpleName() };

	    throw new ElementInvalidException("{exception.ElementInvalidDataSpecification}", ex, params);
	}
    }
}
