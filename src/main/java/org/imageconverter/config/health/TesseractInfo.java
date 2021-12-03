package org.imageconverter.config.health;

import static org.apache.commons.collections.MapUtils.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.imageconverter.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

@Component
@Endpoint(id = "tesseract")
// @Endpoint, @JmxEndpoint, and @WebEndpoint
public class TesseractInfo {

    @Value("classpath:check-image.png")
    private Resource imageFile;

    @Autowired
    private RefreshEndpoint refreshEndpoint;

    @ReadOperation
    public TesseractDetails read() {

	final var details = new LinkedHashMap<String, Object>();

	final var tesseract = BeanUtil.getBeanFrom(ITesseract.class);
	
	details.put("tesseractVersion", "4.11");
	details.put("tesseractFolder", BeanUtil.getEnvironment().getProperty("tesseract.folder"));
	details.put("tesseractLanguage", BeanUtil.getEnvironment().getProperty("tesseract.language"));
	details.put("tesseractDpi", BeanUtil.getEnvironment().getProperty("tesseract.dpi"));
	
	if (check(tesseract)) {
	    details.put("tesseractInit", "SUCCESSFUL");
	} else {
	    details.put("tesseractInit", "FAIL");
	}

	return new TesseractDetails(details);
    }

//    @ReadOperation
//    public String health(@Selector String name) {
//	return "custom-end-point";
//    }

    @WriteOperation
    public void writeOperation(@Nullable final String tesseractFolder, @Nullable final String tesseractLanguage, @Nullable final String tesseractDpi) {
	final var env = (ConfigurableEnvironment) BeanUtil.getEnvironment();

	final var propertySources = env.getPropertySources();
	final var map = new HashMap<String, Object>();

	if (Objects.nonNull(tesseractFolder)) {
	    map.put("tesseract.folder", tesseractFolder);
	}

	if (Objects.nonNull(tesseractLanguage)) {
	    map.put("ltesseract.anguage", tesseractLanguage);
	}

	if (Objects.nonNull(tesseractDpi)) {
	    map.put("tesseract.dpi", tesseractDpi);
	}

	propertySources.addFirst(new MapPropertySource("newmap", map));

	if (!map.isEmpty()) {
	    refreshEndpoint.refresh();
	}

    }

//    @DeleteOperation
//    public void deleteOperation(@Selector final String name) {
//	// delete operation
//    }

    private boolean check(final ITesseract tesseract) {

	try {
	    final var numberOne = tesseract.doOCR(imageFile.getFile()).replaceAll("\\D+", "");

	    if (equalsIgnoreCase(numberOne, "033")) {
		return true;
	    }

	} catch (final TesseractException | IOException e) {
	    return false;
	}

	return false;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TesseractDetails {

	private final Map<String, Object> tesseractDetails;

	TesseractDetails(final Map<String, Object> tesseractDetails) {
	    super();
	    this.tesseractDetails = tesseractDetails;
	}
	
	@SuppressWarnings("unchecked")
	@JsonAnyGetter
	public Map<String, Object> getTesseractDetails() {
	    return unmodifiableMap(tesseractDetails);
	}
    }
}
