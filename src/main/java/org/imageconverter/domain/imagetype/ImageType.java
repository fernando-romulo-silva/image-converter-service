package org.imageconverter.domain.imagetype;

import static javax.persistence.GenerationType.IDENTITY;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.imageconverter.util.BeanUtil.getBeanFrom;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "IMAGE_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = "IMT_EXTENSION", name = "UK_IMT_EXTENSION"))
public class ImageType {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMT_ID", nullable = false)
    private Long id;

    // PNG, BMP, JPEG, JPG ...
    @NotEmpty(message = "The 'extension' cannot be empty")
    @Column(name = "IMT_EXTENSION", nullable = false, unique = true)
    private String extension;

    @NotEmpty(message = "The 'name' cannot be empty")
    @Column(name = "IMT_NAME", nullable = false)
    private String name;

    @Column(name = "IMT_DESC", nullable = true)
    private String description;

    @Column(name = "IMT_CREATED", nullable = false)
    private LocalDateTime created;

    @Column(name = "IMT_UPDATED", nullable = true)
    private LocalDateTime updated;

    ImageType() {
	super();
    }

    public ImageType( //
		    final String extension, //
		    final String name, //
		    final String description) {
	super();
	this.extension = extension;
	this.name = name;
	this.description = description;
	this.created = LocalDateTime.now();

	final var validator = getBeanFrom(Validator.class);

	final var violations = validator.validate(this);
	if (!violations.isEmpty()) {
	    throw new ConstraintViolationException(violations);
	}
    }

    public void update(final String extension, final String name, final String description) {

	if (isNoneBlank(extension)) {
	    this.extension = extension;
	}

	if (isNoneBlank(name)) {
	    this.name = name;
	}

	if (isNoneBlank(description)) {
	    this.description = description;
	}

	this.updated = LocalDateTime.now();
    }

    public Long getId() {
	return id;
    }

    public String getExtension() {
	return extension;
    }

    public String getName() {
	return name;
    }

    public Optional<String> getDescription() {
	return Optional.ofNullable(description);
    }

    public LocalDateTime getCreated() {
	return LocalDateTime.from(created);
    }

    public Optional<LocalDateTime> getUpdated() {
	return Optional.ofNullable(updated);
    }

    @Override
    public int hashCode() {
	return Objects.hash(this.id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;
	    
	} else if (obj instanceof ImageType other) {
	    result = Objects.equals(this.id, other.id);
	    
	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder(34);
	builder.append("ImageType [id=").append(id) //
			.append(", extension=").append(extension) //
			.append(", name=").append(name) //
			.append(']');
	return builder.toString();
    }
}
