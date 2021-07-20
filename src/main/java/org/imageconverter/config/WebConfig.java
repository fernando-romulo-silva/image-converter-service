package org.imageconverter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {

	final var urlPathHelper = new UrlPathHelper();
	urlPathHelper.setRemoveSemicolonContent(false);
	configurer.setUrlPathHelper(urlPathHelper);
    }
}
