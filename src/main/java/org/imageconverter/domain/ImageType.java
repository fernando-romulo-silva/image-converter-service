package org.imageconverter.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "IMAGE_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = "IMT_EXTENSION", name = "UK_IMT_EXTENSION"))
public class ImageType {

    private Long id;

    private String extension;

    private String name;

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

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMT_ID", nullable = false)
    public Long getId() {
	return id;
    }

    // PNG, BMP, JPEG, JPG ...
    @NotBlank
    @Column(name = "IMT_EXTENSION", nullable = false)
    public String getExtension() {
	return extension;
    }

    @NotBlank
    @Column(name = "IMT_NAME", nullable = false)
    public String getName() {
	return name;
    }

    @Column(name = "IMT_NAME", nullable = true) 
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
