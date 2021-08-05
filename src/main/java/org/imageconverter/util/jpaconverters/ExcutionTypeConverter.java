package org.imageconverter.util.jpaconverters;

import static org.apache.commons.lang3.StringUtils.upperCase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.imageconverter.domain.ExecutionType;

@Converter(autoApply = true)
public class ExcutionTypeConverter implements AttributeConverter<ExecutionType, String> {

    @Override
    public String convertToDatabaseColumn(final ExecutionType attribute) {
	return upperCase(attribute.toString());
    }

    @Override
    public ExecutionType convertToEntityAttribute(final String dbData) {
	return ExecutionType.from(dbData);
    }
}
