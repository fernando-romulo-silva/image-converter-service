package org.imageconverter.config;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.MapperFeature.ALLOW_COERCION_OF_SCALARS;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

/**
 * Web configuration bean.
 * 
 * @author Fernando Romulo da Silva
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableConfigurationProperties
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {

	final var urlPathHelper = new UrlPathHelper();
	urlPathHelper.setRemoveSemicolonContent(false);
	configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	registry.addResourceHandler("/swagger-ui/**")//
			.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/4.1.3/");
    }

    /**
     * Create a valid {@link JsonMapper} object configured.
     * 
     * @return A {@link JsonMapper} object
     */
    @Bean
    @Primary
    JsonMapper objectMapper() {
	final var module = new JavaTimeModule();
	module.addSerializer(new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
	module.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
	module.addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME));

	return (JsonMapper) JsonMapper.builder() //
			.configure(ALLOW_COERCION_OF_SCALARS, false) //
			.serializationInclusion(NON_NULL) //
			.addModule(module) //
			.build();
    }

    /**
     * Create a valid {@link CommonsMultipartResolver} object configured (max upload and default encoding).
     * 
     * @return A {@link CommonsMultipartResolver} object
     */
    @Bean
    CommonsMultipartResolver multipartResolver() {
	final var resolver = new CommonsMultipartResolver();
	resolver.setDefaultEncoding("utf-8");
//        resolver.setMaxInMemorySize();
//        resolver.setMaxUploadSize(ofMegabytes(20));
	return resolver;
    }

//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//	InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//	viewResolver.setViewClass(JstlView.class);
//	viewResolver.setPrefix("/WEB-INF/views/");
//	viewResolver.setSuffix(".jsp");
//	return viewResolver;
//    }
//
//
//    @Override
//    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
//	registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
//    }
}
