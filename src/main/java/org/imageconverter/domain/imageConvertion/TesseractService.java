package org.imageconverter.domain.imageConvertion;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ImageConvertServiceException;
import org.imageconverter.infra.exceptions.TesseractConvertionException;
import org.imageconverter.util.controllers.ImageConverterRequestArea;
import org.imageconverter.util.controllers.ImageConverterRequestInterface;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
@Loggable
public class TesseractService {

    private final Tesseract tesseractTess4j;

    @Autowired
    TesseractService(final Tesseract tesseractTess4j) {
	super();
	this.tesseractTess4j = tesseractTess4j;
    }

    // @PreAuthorize("hasAuthority('ADMIN') or @accessChecker.hasLocalAccess(authentication)")
    public String convert(final ImageConverterRequestInterface imageInterface) {

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(imageInterface.data().getBytes()));

	    final String result;

	    if (imageInterface instanceof ImageConverterRequestArea image && nonNull(image.x()) && nonNull(image.y()) && nonNull(image.width()) && nonNull(image.height())) {
		
		result = tesseractTess4j.doOCR(bufferedImage, new Rectangle(image.x(), image.y(), image.width(), image.height()));
		
	    } else {
		
		result = tesseractTess4j.doOCR(bufferedImage);
	    }

	    return result;
	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error {1}.", imageInterface, getRootCauseMessage(ex));
	    throw new ConvertionException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error {1}.", getRootCauseMessage(ex));
	    throw new TesseractConvertionException(msg, ex);

	} catch (final Throwable ex) {

	    final var msg = format("Unexpected error {0}.", getRootCauseMessage(ex));
	    throw new ImageConvertServiceException(msg, ex);
	}
    }
}