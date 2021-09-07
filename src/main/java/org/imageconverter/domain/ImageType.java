package org.imageconverter.domain;

import static javax.persistence.GenerationType.IDENTITY;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "IMAGE_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = "IMT_EXTENSION", name = "UK_IMT_EXTENSION"))
public class ImageType {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMT_ID", nullable = false)
    private Long id;

    // PNG, BMP, JPEG, JPG ...
    @NotBlank
    @Column(name = "IMT_EXTENSION", nullable = false, unique = true)
    private String extension;

    @NotBlank
    @Column(name = "IMT_NAME", nullable = false)
    private String name;

    @Column(name = "IMT_DESC", nullable = true)
    private String description;

    ImageType() {
	super();
    }

    public ImageType(final String extension, final String name, final String description) {
	super();
	this.extension = extension;
	this.name = name;
	this.description = description;
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

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj)
	    return true;

	if (!(obj instanceof ImageType other))
	    return false;

	return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();
	builder.append("ImageType [id=").append(id) //
			.append(", extension=").append(extension) //
			.append(", name=").append(name) //
			.append("]");
	return builder.toString();
    }
}
