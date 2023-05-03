package org.imageconverter.config.health;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
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

/**
 * Component to show informations about tesseract configs.
 * 
 * @author Fernando Romulo da Silva
 */
@Component
@Endpoint(id = "tesseract")
// @Endpoint, @JmxEndpoint, and @WebEndpoint
public class TesseractInfoService {

    private final Resource imageFile;

    private final RefreshEndpoint refreshEndpoint;

    /**
     * Default constructor.
     * 
     * @param newImageFile       Image to check if the tesseract works
     * @param newRefreshEndpoint Represh point to update infos
     */
    TesseractInfoService( //
		    @Value("classpath:check-image.png") //
		    final Resource newImageFile, //
		    //
		    @Autowired //
		    final RefreshEndpoint newRefreshEndpoint) {
	super();
	this.imageFile = newImageFile;
	this.refreshEndpoint = newRefreshEndpoint;
    }

    /**
     * Read data about tesseract configs.
     * 
     * @return A {@link TesseractDetailsData} with the data
     */
    @ReadOperation
    public TesseractDetailsData read() {

	final var details = new LinkedHashMap<String, Object>();

	details.put("tesseractVersion", "4.11");
	details.put("tesseractFolder", BeanUtil.getPropertyValue("tesseract.folder"));
	details.put("tesseractLanguage", BeanUtil.getPropertyValue("tesseract.language"));
	details.put("tesseractDpi", BeanUtil.getPropertyValue("tesseract.dpi"));

	if (checkTesseract()) {
	    details.put("tesseractInit", "SUCCESSFUL");
	} else {
	    details.put("tesseractInit", "FAIL");
	}

	return new TesseractDetailsData(details);
    }

//    @ReadOperation
//    public String health(@Selector String name) {
//	return "custom-end-point";
//    }

    /**
     * Update values from request http, JMX, etc.
     * 
     * @param tesseractFolder   The tesseract intalled dictionary
     * @param tesseractLanguage The tesseract language
     * @param tesseractDpi      The resolution quality
     */
    @WriteOperation
    public void writeOperation(@Nullable final String tesseractFolder, @Nullable final String tesseractLanguage, @Nullable final String tesseractDpi) {
	final var env = (ConfigurableEnvironment) BeanUtil.getEnvironment();

	final var propertySources = env.getPropertySources();
	final var map = new HashMap<String, Object>();

	if (Objects.nonNull(tesseractFolder)) {
	    map.put("tesseract.folder", tesseractFolder);
	}

	if (Objects.nonNull(tesseractLanguage)) {
	    map.put("tesseract.language", tesseractLanguage);
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

    private boolean checkTesseract() {

	final var tesseractBeanProvider = BeanUtil.getBeanProviderFrom(ITesseract.class);
	final var tesseract = tesseractBeanProvider.getObject();

	boolean result = false;

	if (Objects.isNull(tesseract)) {

	    result = false;

	} else {

	    try {

		final var numberOne = tesseract.doOCR(imageFile.getFile()).replaceAll("\\D+", "");

		if (equalsIgnoreCase(numberOne, "033")) {
		    result = true;
		}

	    } catch (final TesseractException | IOException | IllegalArgumentException ex) {
		result = false;
	    }
	}

	return result;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TesseractDetailsData {

	private final Map<String, Object> tesseractDetails;

	TesseractDetailsData(final Map<String, Object> tesseractDetails) {
	    super();
	    this.tesseractDetails = tesseractDetails;
	}

	@JsonAnyGetter
	public Map<String, Object> getTesseractDetails() {
	    return MapUtils.unmodifiableMap(tesseractDetails);
	}
    }
}
