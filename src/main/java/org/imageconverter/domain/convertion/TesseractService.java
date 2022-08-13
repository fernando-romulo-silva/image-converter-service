package org.imageconverter.domain.convertion;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.imageconverter.infra.exception.ConvertionException;
import org.imageconverter.infra.exception.TesseractConvertionException;
import org.imageconverter.infra.exception.TesseractNotSetException;
import org.imageconverter.util.BeanUtil;
import org.imageconverter.util.logging.Loggable;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * A Java class to access tesseract application
 * 
 * @author Fernando Romulo da Silva
 */
@Service
@Loggable
public class TesseractService {

    // @PreAuthorize("hasAuthority('ADMIN') or @accessChecker.hasLocalAccess(authentication)")
    /**
     * Convert a file image to text.
     * 
     * @param fileName  The file's name
     * @param fileBytes A file that will be convert
     * @return A {@link String} object with the conversion
     */
    public String convert(final String fileName, final byte[] fileBytes) {

	final var tesseractTess4j = BeanUtil.getBeanProviderFrom(ITesseract.class).getIfAvailable();

	if (Objects.isNull(tesseractTess4j)) {
	    throw new TesseractNotSetException();
	}

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(fileBytes));

	    return tesseractTess4j.doOCR(bufferedImage);

	} catch (final IllegalArgumentException ex) {

	    throw new TesseractNotSetException(ex);

	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error: ''{1}''.", fileName, getRootCauseMessage(ex));
	    throw new ConvertionException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error: ''{1}''.", fileName, getRootCauseMessage(ex));
	    throw new TesseractConvertionException(msg, ex);

//	} catch (final Exception ex) {
//
//	    final var msg = format("Unexpected error: ''{0}''.", getRootCauseMessage(ex));
//	    throw new ImageConvertServiceException(msg, ex);
	}
    }

    /**
     * Convert a file image to text.
     * 
     * @param fileName  The file's name
     * @param fileBytes A file that will be convert
     * @param xAxis     The image's x coordinate
     * @param yAxis     The image's y coordinate
     * @param width     The image's width in pixels
     * @param height    The image's height in pixels
     * 
     * @return A {@link String} object with the conversion
     */
    public String convert(final String fileName, final byte[] fileBytes, final int xAxis, final int yAxis, final int width, final int height) {

	final var tesseractTess4j = BeanUtil.getBeanProviderFrom(ITesseract.class).getIfAvailable();

	if (Objects.isNull(tesseractTess4j)) {
	    throw new TesseractNotSetException();
	}

	try {

	    final var bufferedImage = ImageIO.read(new ByteArrayInputStream(fileBytes));

	    return tesseractTess4j.doOCR(bufferedImage, new Rectangle(xAxis, yAxis, width, height));

	} catch (final IllegalArgumentException ex) {

	    throw new TesseractNotSetException(ex);

	} catch (final IOException ex) {

	    final var msg = format("Image {0} has IO error: ''{1}'', X {2}, Y {3}, Width {4} and Heigh {5}.", fileName, getRootCauseMessage(ex), xAxis, yAxis, width, height);
	    throw new ConvertionException(msg, ex);

	} catch (final TesseractException | Error ex) {

	    final var msg = format("Image {0} has Tessarct error: ''{1}'', X {2}, Y {3}, Width {4} and Heigh {5}.", fileName, getRootCauseMessage(ex), xAxis, yAxis, width, height);
	    throw new TesseractConvertionException(msg, ex);

//	} catch (final Exception ex) {
//
//	    final var msg = format("Unexpected error: ''{0}'', X {1}, Y {2}, Width {3} and Heigh {4}.", getRootCauseMessage(ex), xAxis, yAxis, width, height);
//	    throw new ImageConvertServiceException(msg, ex);
	}
    }
}