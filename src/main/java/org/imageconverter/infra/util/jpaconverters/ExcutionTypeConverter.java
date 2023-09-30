package org.imageconverter.infra.util.jpaconverters;

import static org.apache.commons.lang3.StringUtils.upperCase;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.imageconverter.domain.conversion.ExecutionType;

/**
 * Converter for {@link ExecutionType} enum to String for JPA framework.
 * 
 * @author Fernando Romulo da Silva
 */
@Converter(autoApply = true)
public class ExcutionTypeConverter implements AttributeConverter<ExecutionType, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToDatabaseColumn(final ExecutionType attribute) {
	return upperCase(attribute.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionType convertToEntityAttribute(final String dbData) {
	return ExecutionType.from(dbData);
    }
}
