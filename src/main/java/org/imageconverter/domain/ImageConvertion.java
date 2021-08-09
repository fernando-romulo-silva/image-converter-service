package org.imageconverter.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "IMAGE_CONVERTION")
public class ImageConvertion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMGC_ID")
    private Long id;

    @Column(name = "IMGC_NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "IMT_ID", foreignKey = @ForeignKey(name = "FK_IMT_IMGC"))
    private ImageType type;

    @Column(name = "IMGC_SIZE")
    private Long size;

    @Column(name = "IMGC_DATE")
    private LocalDate executionDate;

    @NotNull
    @Column(name = "IMGC_TYPE")
    private ExecutionType executionType;

    @Column(name = "IMGC_TEXT")
    private String text;

    ImageConvertion() {
	super();
    }

    private ImageConvertion(final Builder builder) {
	super();
	this.name = builder.name;
	this.type = builder.type;
	this.size = builder.size;
	this.executionType = builder.executionType;
	this.text = builder.text;
	this.executionDate = LocalDate.now();
    }

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public ImageType getType() {
	return type;
    }

    public Long getSize() {
	return size;
    }

    public LocalDate getExecutionDate() {
	return executionDate;
    }

    public ExecutionType getExecutionType() {
	return executionType;
    }

    public String getText() {
	return text;
    }

    @Override
    public int hashCode() {
	return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj) {
	    return true;
	}

	if (!(obj instanceof ImageConvertion other)) {
	    return false;
	}

	return Objects.equals(name, other.name) && type == other.type;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();

	builder.append("ImageConvertion [id=").append(id) //
			.append(", name=").append(name) //
			.append(", type=").append(type) //
			.append(", size=").append(size)//
			.append(", executionDate=").append(executionDate) //
			.append(", executionType=").append(executionType) //
			.append("]");

	return builder.toString();
    }

    public static final class Builder {

	public String name;

	public ImageType type;

	public Long size;

	public ExecutionType executionType;

	public String text;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public ImageConvertion build() {

	    return new ImageConvertion(this);
	}
    }

}
