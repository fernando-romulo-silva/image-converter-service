package org.imageconverter.config.health;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imageconverter.util.BeanUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Component("tesseract")
public class TesseractHealth implements HealthIndicator {

    @Value("classpath:check-image.png")
    private Resource imageFile;

    @Override
    public Health health() {

	final var tesseract = BeanUtil.getBeanFrom(Tesseract.class);

	final var error = check(tesseract);

	if (StringUtils.isNotBlank(error)) {
	    return Health.down().withDetail("Error", error).build();
	}

	return Health.up().build();
    }

    private String check(final Tesseract tesseract) {

	try {
	    final var numberOne = tesseract.doOCR(imageFile.getFile()).replaceAll("\\D+", "");

	    if (StringUtils.equalsIgnoreCase(numberOne, "033")) {
		return StringUtils.EMPTY;
	    }

	} catch (final TesseractException | IOException ex) {
	    return ExceptionUtils.getRootCauseMessage(ex);
	}

	return "Tesseract isn't work";
    }
}
