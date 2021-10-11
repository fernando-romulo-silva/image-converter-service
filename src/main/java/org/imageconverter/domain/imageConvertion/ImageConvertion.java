package org.imageconverter.domain.imageConvertion;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.persistence.GenerationType.IDENTITY;
import static org.apache.commons.io.FilenameUtils.getExtension;

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
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.imageconverter.domain.imageType.ImageType;
import org.imageconverter.domain.imageType.ImageTypeRespository;
import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ElementNotFoundException;
import org.imageconverter.util.BeanUtil;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestArea;
import org.imageconverter.util.controllers.imageconverter.ImageConverterRequestInterface;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "IMAGE_CONVERTION")
public class ImageConvertion {

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

    @Override
    public int hashCode() {
	return Objects.hash(fileName, fileType);
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj) {
	    return true;
	}

	if (!(obj instanceof ImageConvertion other)) {
	    return false;
	}

	return Objects.equals(fileName, other.fileName) && fileType == other.fileType;
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

	@NotNull(message = "The 'data' cannot be null")
	public MultipartFile data;

	@Min(value = 0, message = "The axis 'x' cannot be less than zero")
	public Integer x;

	@Min(value = 0, message = "The axis 'y' cannot be less than zero")
	public Integer y;

	@Min(value = 0, message = "The 'width' cannot be less than zero")
	public Integer width;

	@Min(value = 0, message = "The 'height' cannot be less than zero")
	public Integer height;

	// --------------------------------------------------------------------------
	@NotNull(message = "The 'fileType' cannot be null")
	private ImageType fileType;

	@NotEmpty(message = "The 'fileName' cannot be empty")
	private String fileName;

	@Min(value = 0, message = "The 'fileSize' cannot be less than zero")
	@NotNull(message = "The 'fileSize' can't be null")
	private Long fileSize;

	@NotEmpty(message = "The 'fileName' cannot be empty")
	private String text;

	private Boolean area;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);

	    return this;
	}

	public Builder with(final ImageConverterRequestInterface request) {

	    this.data = request.data();
	    this.executionType = request.executionType();

	    if (request instanceof ImageConverterRequestArea image //
			    && nonNull(image.x()) && nonNull(image.y()) //
			    && nonNull(image.width()) && nonNull(image.height())) {

		this.x = image.x();
		this.y = image.y();
		this.width = image.width();
		this.height = image.height();
	    }

	    return this;
	}

	public ImageConvertion build() {

	    final var tesseractService = BeanUtil.getBean(TesseractService.class);

	    final var imageTypeRepository = BeanUtil.getBean(ImageTypeRespository.class);

	    if (isNull(data) || data.isEmpty()) {
		throw new ConvertionException("Empty file to convert!");
	    }

	    final var extensionTxt = getExtension(data.getOriginalFilename());

	    this.fileType = imageTypeRepository.findByExtension(extensionTxt) //
			    .orElseThrow(() -> new ElementNotFoundException(ImageType.class, "extension " + extensionTxt));

	    this.fileName = data.getName() + "." + extensionTxt;
	    this.fileSize = data.getSize() / 1024;

	    if (nonNull(x) && nonNull(y) && nonNull(width) && nonNull(height)) {
		this.text = tesseractService.convert(data, x, y, width, height);
		this.area = true;
	    } else {
		this.text = tesseractService.convert(data);
		this.area = false;
	    }

	    return new ImageConvertion(this);
	}
    }

}
