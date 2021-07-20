package org.imageconverter.config;

import static org.springframework.util.unit.DataSize.ofMegabytes;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
public class ImageConfig {

    @Autowired
    private MultipartProperties multipartProperties;

    @Bean
    @ConditionalOnMissingBean({ MultipartConfigElement.class, CommonsMultipartResolver.class })
    @Description("MultipartConfig for file posting")
    public MultipartConfigElement multipartConfigElement() {
        multipartProperties.setEnabled(true);
	multipartProperties.setMaxFileSize(ofMegabytes(20));
        multipartProperties.setMaxRequestSize(ofMegabytes(20));
        return multipartProperties.createMultipartConfig();
    }

    @Bean
    @Order(0)
    @Description("Multipart filter for file posting")
    public MultipartFilter multipartFile() {
        final MultipartFilter multipartFilter = new MultipartFilter();
        // same name's request parameter => final @RequestParam MultipartFile file
        multipartFilter.setMultipartResolverBeanName("file");
        return multipartFilter;
    }

    // @Bean
    // public RestTemplate restTemplate() {
    // SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    // Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8888));
    // requestFactory.setProxy(proxy);
    // return new RestTemplate(requestFactory);
    // }
    //
    // @Bean
    // public ClientHttpRequestFactory clientHttpRequestFactory() {
    //
    // List<ClientHttpRequestInterceptor> interceptors = Arrays.asList();
    //
    // SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    //
    // Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8888));
    //
    // requestFactory.setProxy(proxy);
    //
    // return new InterceptingClientHttpRequestFactory(requestFactory, interceptors);
    // }
    //
    // @Bean
    // public RestTemplateBuilder restTemplateBuilder() {
    // return new RestTemplateBuilder();
    // }
}
