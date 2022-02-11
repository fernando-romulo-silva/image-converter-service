package org.imageconverter.domain.convertion;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.persistence.GenerationType.IDENTITY;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.imageconverter.util.BeanUtil.getBeanFrom;

import java.time.LocalDateTime;
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
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imagetype.ImageType;
import org.imageconverter.domain.imagetype.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "IMAGE_CONVERTION")
public class ImageConvertion { // NOPMD - Provide accessors on private constructor

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMGC_ID")
    private Long id;

    @Column(name = "IMGC_NAME")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "IMT_ID", foreignKey = @ForeignKey(name = "FK_IMT_IMGC"))
    private ImageType fileType;

    @Column(name = "IMGC_SIZE")
    private Long fileSize;

    @NotNull
    @Column(name = "IMGC_TYPE")
    private ExecutionType type;

    @Column(name = "IMGC_TEXT")
    private String text;

    @Column(name = "IMGC_AREA")
    private Boolean area;

    @Column(name = "IMGC_CREATED")
    private LocalDateTime created;

    ImageConvertion() {
	super();
    }

    private ImageConvertion(@Valid final Builder builder) {
	super();
	this.fileName = builder.fileName;
	this.fileType = builder.fileType;
	this.fileSize = builder.fileSize;
	this.type = builder.executionType;
	this.text = builder.text;
	this.area = builder.area;

	this.created = LocalDateTime.now();
    }

    public Long getId() {
	return id;
    }

    public String getFileName() {
	return fileName;
    }

    public ImageType getFileType() {
	return fileType;
    }

    public Long getFileSize() {
	return fileSize;
    }

    public LocalDateTime getCreated() {
	return created;
    }

    public ExecutionType getType() {
	return type;
    }

    public String getText() {
	return text;
    }

    public Boolean getArea() {
	return area;
    }

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj) {
	    return true;
	}

	if (!(obj instanceof ImageConvertion other)) {
	    return false;
	}

	return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
	final var sb = new StringBuilder();

	sb.append("ImageConvertion [id=").append(id) //
			.append(", fileName=").append(fileName) //
			.append(", fileType=").append(fileType) //
			.append(", fileSize=").append(fileSize) //
			.append(", created=").append(created) //
			.append(", type=").append(type).append("]");

	return sb.toString();
    }

    public static final class Builder {

	@NotNull(message = "The 'fileType' cannot be null")
	public ExecutionType executionType;

	@NotNull(message = "The 'file' cannot be null")
	public MultipartFile file;

	@Min(value = 0, message = "The axis 'x' cannot be less than zero")
	public Integer xAxis;

	@Min(value = 0, message = "The axis 'y' cannot be less than zero")
	public Integer yAxis;

	@Min(value = 0, message = "The 'width' cannot be less than zero")
	public Integer width;

	@Min(value = 0, message = "The 'height' cannot be less than zero")
	public Integer height;

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	@NotNull(message = "The 'fileType' cannot be null")
	private ImageType fileType;

	@NotEmpty(message = "The 'fileName' cannot be empty")
	private String fileName;

	@Min(value = 0, message = "The 'fileSize' cannot be less than zero")
	@NotNull(message = "The 'fileSize' can't be null")
	private Long fileSize;

	@NotEmpty(message = "The 'text' cannot be empty")
	private String text;

	private Boolean area;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);

	    return this;
	}

	public Builder with(final ImageConverterRequestInterface request) {

	    if (isNull(request)) {
		throw new ConvertionException("Empty request to convert!");
	    }
	    
	    this.file = request.file();
	    this.executionType = request.executionType();

	    if (request instanceof ImageConverterRequestArea image //
			    && nonNull(image.xAxis()) && nonNull(image.yAxis()) //
			    && nonNull(image.width()) && nonNull(image.height())) {

		this.xAxis = image.xAxis();
		this.yAxis = image.yAxis();
		this.width = image.width();
		this.height = image.height();
	    }

	    return this;
	}

	public ImageConvertion build() {

	    if (isNull(file) || file.isEmpty()) {
		throw new ConvertionException("Empty file to convert!");
	    }
	    
	    final var tesseractService = getBeanFrom(TesseractService.class);

	    final var imageTypeRepository = getBeanFrom(ImageTypeRespository.class);

	    final var validator = getBeanFrom(Validator.class);

	    final var extensionTxt = getExtension(file.getOriginalFilename());

	    this.fileType = imageTypeRepository.findByExtension(extensionTxt) //
			    .orElseThrow(() -> new ElementNotFoundException(ImageType.class, "extension " + extensionTxt));

	    this.fileName = file.getOriginalFilename();
	    this.fileSize = file.getSize() / 1024;

	    if (nonNull(xAxis) && nonNull(yAxis) && nonNull(width) && nonNull(height)) {
		this.text = tesseractService.convert(file, xAxis, yAxis, width, height);
		this.area = true;
	    } else {
		this.text = tesseractService.convert(file);
		this.area = false;
	    }

	    final var violations = validator.validate(this);
	    if (!violations.isEmpty()) {
		throw new ConstraintViolationException(violations);
	    }

	    return new ImageConvertion(this);
	}
    }

}
