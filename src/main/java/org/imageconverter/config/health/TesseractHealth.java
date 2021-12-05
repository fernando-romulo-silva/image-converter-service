package org.imageconverter.config.health;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imageconverter.util.BeanUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

@Component("tesseract")
public class TesseractHealth implements HealthIndicator {

    @Value("classpath:check-image.png")
    private Resource imageFile;

    @Override
    public Health health() {

	final var error = checkTesseract();

	if (StringUtils.isNotBlank(error)) {
	    return Health.down().withDetail("Error", error).build();
	}

	return Health.up().build();
    }

    private String checkTesseract() {
	
	final var tesseract = BeanUtil.getBeanProviderFrom(ITesseract.class).getIfAvailable();
	
	if (Objects.isNull(tesseract)) {
	    return "Tesseract isn't work";
	}

	try {
	    
	    final var numberOne = tesseract.doOCR(imageFile.getFile()).replaceAll("\\D+", "");

	    if (StringUtils.equalsIgnoreCase(numberOne, "033")) {
		return StringUtils.EMPTY;
	    }

	} catch (final IllegalArgumentException ex) {
	    return "Tesseract isn't work";
	} catch (final TesseractException | IOException ex) {
	    return ExceptionUtils.getRootCauseMessage(ex);
	}

	return "Tesseract isn't work";
    }
}
