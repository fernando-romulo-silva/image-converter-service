package org.imageconverter.config;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.MapperFeature.ALLOW_COERCION_OF_SCALARS;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableTransactionManagement
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
	module.addSerializer(new LocalTimeSerializer(DateTimeFormatter.ISO_TIME));

	final var objectMapper = new ObjectMapper();

	objectMapper.setSerializationInclusion(NON_NULL) //
			.configure(ALLOW_COERCION_OF_SCALARS, false) //
			.registerModule(module);

//	mapper.registerSubtypes(null);
	
	return objectMapper;
    }
    
    @Bean
    public CommonsMultipartResolver multipartResolver() {
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
