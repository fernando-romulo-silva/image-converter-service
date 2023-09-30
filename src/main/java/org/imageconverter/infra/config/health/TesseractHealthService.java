package org.imageconverter.infra.config.health;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imageconverter.infra.util.BeanUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * Tesseract health checker.
 * 
 * @author Fernando Romulo da Silva
 */
@Component("tesseract")
public class TesseractHealthService implements HealthIndicator {

    private final Resource imageFile;

    /**
     * Default constructor.
     * 
     * @param imageFile image used for tesseract check
     */
    public TesseractHealthService(@Value("classpath:check-image.png") final Resource imageFile) {
	super();
	this.imageFile = imageFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Health health() {

	final var error = checkTesseract();

	final Health result;

	if (StringUtils.isNotBlank(error)) {
	    result = Health.down().withDetail("Error", error).build();
	} else {
	    result = Health.up().build();
	}

	return result;
    }

    /**
     * Check if tesseract is working by executing a test.
     * 
     * @return Empty string if tesseract is working or a error message.
     */
    private String checkTesseract() {

	final var tesseractBeanProvider = BeanUtil.getBeanProviderFrom(ITesseract.class);
	final var tesseract = tesseractBeanProvider.getObject();

	String result;

	if (Objects.isNull(tesseract)) {

	    result = "Tesseract isn't configured";

	} else {

	    try {

		final var bufferedImage = ImageIO.read(new ByteArrayInputStream(imageFile.getInputStream().readAllBytes()));
		
		final var numberOne = tesseract.doOCR(bufferedImage).replaceAll("\\D+", "");

		if (StringUtils.equalsIgnoreCase(numberOne, "033")) {
		    result = StringUtils.EMPTY;
		} else {
		    result = "Tesseract isn't work";
		}

	    } catch (final IllegalArgumentException ex) {

		result = "Tesseract has configurations issues";

	    } catch (final TesseractException | IOException ex) {
		result = ExceptionUtils.getRootCauseMessage(ex);
	    }

	}

	return result;
    }
}
