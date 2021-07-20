package org.imageconverter.util;

import static org.apache.commons.lang3.StringUtils.upperCase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.imageconverter.domain.ImageType;

@Converter(autoApply = true)
public class ImageTypeConverter implements AttributeConverter<ImageType, String> {

    @Override
    public String convertToDatabaseColumn(final ImageType attribute) {
	return upperCase(attribute.toString());
    }

    @Override
    public ImageType convertToEntityAttribute(final String dbData) {
	return ImageType.from(dbData);
    }
}
