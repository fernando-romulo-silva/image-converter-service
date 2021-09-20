package org.imageconverter.config.health;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Component(value = "tesseract")
public class TesseractHealth implements HealthIndicator, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Value("classpath:check-image.png")
    private Resource imageFile;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
    }

    @Override
    public Health health() {

	final var tesseract = applicationContext.getBean(Tesseract.class);

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
