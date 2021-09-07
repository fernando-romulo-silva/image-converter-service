package org.imageconverter.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {

	final var urlPathHelper = new UrlPathHelper();
	urlPathHelper.setRemoveSemicolonContent(false);
	configurer.setUrlPathHelper(urlPathHelper);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
	final var module = new JavaTimeModule();
	module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
	module.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));

	final var mapper = new ObjectMapper();

	mapper.setSerializationInclusion(Include.NON_NULL) //
			.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false) //
			.registerModule(module);

	return mapper;
    }
}
