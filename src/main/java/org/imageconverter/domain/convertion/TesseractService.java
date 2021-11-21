package org.imageconverter.domain.convertion;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imageconverter.infra.exceptions.ConvertionException;
import org.imageconverter.infra.exceptions.ImageConvertServiceException;
import org.imageconverter.infra.exceptions.TesseractConvertionException;
import org.imageconverter.util.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
@Loggable
public class TesseractService {

    private final ITesseract tesseractTess4j;

    @Autowired
    TesseractService(final ITesseract tesseractTess4j) {
	super();
	this.tesseractTess4j = tesseractTess4j;
    }

    // @PreAuthorize("hasAuthority('ADMIN') or @accessChecker.hasLocalAccess(authentication)")
    public String convert(final MultipartFile data) {

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(data.getBytes()));

	    return tesseractTess4j.doOCR(bufferedImage);

	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error {1}.", data.getOriginalFilename(), getRootCauseMessage(ex));
	    throw new ConvertionException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error {1}.", getRootCauseMessage(ex));
	    throw new TesseractConvertionException(msg, ex);

	} catch (final Exception ex) {

	    final var msg = format("Unexpected error {0}.", getRootCauseMessage(ex));
	    throw new ImageConvertServiceException(msg, ex);
	}
    }

    public String convert(final MultipartFile data, final int x, final int y, final int width, final int height) {

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(data.getBytes()));

	    return tesseractTess4j.doOCR(bufferedImage, new Rectangle(x, y, width, height));

	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error {1}.", data.getOriginalFilename(), getRootCauseMessage(ex));
	    throw new ConvertionException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error {1}.", getRootCauseMessage(ex));
	    throw new TesseractConvertionException(msg, ex);

	} catch (final Exception ex) {

	    final var msg = format("Unexpected error {0}.", getRootCauseMessage(ex));
	    throw new ImageConvertServiceException(msg, ex);
	}
    }
}