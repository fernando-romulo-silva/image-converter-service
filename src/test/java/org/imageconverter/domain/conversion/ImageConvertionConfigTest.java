package org.imageconverter.domain.conversion;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.validation.Validator;

import org.imageconverter.TestConstants;
import org.imageconverter.domain.imagetype.ImageTypeRespository;
import org.imageconverter.infra.util.BeanUtil;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockMultipartFile;

import net.sourceforge.tess4j.ITesseract;

/**
 * Class base for conversion unit tests.
 * 
 * @author Fernando Romulo da Silva
 */
class ImageConversionConfigTest {

    protected MockMultipartFile mockMultipartFile;

    @Mock
    protected ApplicationContext applicationContext;

    @Mock
    protected ImageTypeRespository imageTypeRespository;

    @Mock
    protected ITesseract tesseractTess4j;

    @Mock
    protected ObjectProvider<ITesseract> objectProvider;

    protected void setUpSuper() throws IOException {

	// ------------------------------------
	final var validator = buildDefaultValidatorFactory().getValidator();

	// ------------------------------------
	final var file = new File("src/test/resources/images/" + TestConstants.FILE_NAME_IMAGE_PNG);

	final var image = ImageIO.read(file);
	final var baos = new ByteArrayOutputStream();

	ImageIO.write(image, "png", baos);
	final var bytes = baos.toByteArray();

	mockMultipartFile = new MockMultipartFile("file", TestConstants.FILE_NAME_IMAGE_PNG, MULTIPART_FORM_DATA_VALUE, bytes);

	// ------------------------------------
	MockitoAnnotations.openMocks(this);

	BeanUtil.defineContext(applicationContext);

	when(applicationContext.getBean(ImageTypeRespository.class))
			.thenReturn(imageTypeRespository);

	when(applicationContext.getBean(Validator.class))
			.thenReturn(validator);

	when(applicationContext.getBeanProvider(ITesseract.class))
			.thenReturn(objectProvider);

	when(objectProvider.getIfAvailable())
			.thenReturn(tesseractTess4j);
	
	when(objectProvider.getObject())
		.thenReturn(tesseractTess4j);

	when(applicationContext.getBean(TesseractService.class))
			.thenReturn(new TesseractService());

	final var messageSource = new ResourceBundleMessageSource();
	messageSource.setBasename("messages");
	messageSource.setDefaultEncoding("UTF-8");

	when(applicationContext.getBean(MessageSource.class))
			.thenReturn(messageSource);
    }

}
