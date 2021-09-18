package org.imageconverter.domain.imageConvertion;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ImageConvertServiceException;
import org.imageconverter.infra.exceptions.TesseractConvertionException;
import org.imageconverter.util.controllers.ImageConverterRequest;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
@Loggable
public class TesseractService {

    private final Tesseract tesseract;

    @Autowired
    TesseractService(final Tesseract tesseract) {
	super();
	this.tesseract = tesseract;
    }

    // @PreAuthorize("hasAuthority('ADMIN') or @accessChecker.hasLocalAccess(authentication)")
    public String convert(final ImageConverterRequest image) {

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(image.data().getBytes()));

	    final String result;

	    if (image.x() > 0 && image.y() > 0 && image.width() > 0 && image.height() > 0) {
		result = tesseract.doOCR(bufferedImage, new Rectangle(image.x(), image.y(), image.width(), image.height()));
	    } else {
		result = tesseract.doOCR(bufferedImage);
	    }

	    return result;
	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error {1}.", image, getRootCauseMessage(ex));
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