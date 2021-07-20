package org.imageconverter.domain;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imageconverter.infra.ConvertionImageException;
import org.imageconverter.infra.ImageConvertServiceException;
import org.imageconverter.infra.TesseractConvertionException;
import org.imageconverter.util.ImageConverterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public final class TesseractService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String tesseractFolder;

    private final String tesseractLanguage;

    TesseractService( //
		    @Value("${tesseract.folder}") //
		    final String tesseractFolder, //
		    //
		    @Value("${tesseract.language}") //
		    final String tesseractLanguage) {
	super();
	this.tesseractFolder = tesseractFolder;
	this.tesseractLanguage = tesseractLanguage;
    }

    // @PreAuthorize("hasAuthority('ADMIN') or @accessChecker.hasLocalAccess(authentication)")
    public String convert(final ImageConverterRequest image) {

	log.info("Starts file {} conversion.", image);

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(image.data().getBytes()));

	    final var tesseract = new Tesseract();

	    tesseract.setDatapath(tesseractFolder);
	    tesseract.setLanguage(tesseractLanguage);

	    final String result;

	    if (image.x() > 0 && image.y() > 0 && image.width() > 0 && image.height() > 0) {
		result = tesseract.doOCR(bufferedImage, new Rectangle(image.x(), image.y(), image.width(), image.height()));
	    } else {
		result = tesseract.doOCR(bufferedImage);
	    }

	    log.info("Ends file {} conversion with result {}.", image, result);

	    return result;
	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error {1}.", image, getRootCauseMessage(ex));
	    log.info("Ends with error: {}", msg);
	    throw new ConvertionImageException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error {1}.", getRootCauseMessage(ex));
	    log.info("Ends with error: {}", msg);
	    throw new TesseractConvertionException(msg, ex);

	} catch (final Throwable ex) {
	    
	    final var msg = format("Unexpected error {0}.", getRootCauseMessage(ex));
	    log.info("Ends with error: {}", msg);
	    throw new ImageConvertServiceException(msg, ex);
	}
    }
}