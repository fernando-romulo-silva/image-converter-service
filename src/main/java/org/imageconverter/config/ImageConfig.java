package org.imageconverter.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.imageconverter.util.BeanUtil;
import org.imageconverter.util.TesseractSupplier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sourceforge.tess4j.Tesseract;

@Configuration
//@EnableConfigurationProperties(MultipartProperties.class)
public class ImageConfig {

    @Bean
    @RefreshScope
    public TesseractSupplier tesseractTess4jSupplier() {

	final var path = Paths.get(BeanUtil.getEnvironment().getProperty("tesseract.folder"));
	
	if (Files.notExists(path)) {
	    return () -> null;
	}
	
	final var tesseract = new Tesseract();
	tesseract.setDatapath(BeanUtil.getEnvironment().getProperty("tesseract.folder"));
	tesseract.setLanguage(BeanUtil.getEnvironment().getProperty("tesseract.language"));
	tesseract.setTessVariable("user_defined_dpi", BeanUtil.getEnvironment().getProperty("tesseract.dpi"));

	return () -> tesseract;
    }

//    @Bean
//    @Primary
//    public Validator validator() {
//	final var factory = Validation.buildDefaultValidatorFactory();
//	return factory.getValidator();
//    }

//    @Autowired
//    private MultipartProperties multipartProperties;

//    @Bean
//    @ConditionalOnMissingBean({ MultipartConfigElement.class, CommonsMultipartResolver.class })
//    @Description("MultipartConfig for file posting")
//    public MultipartConfigElement multipartConfigElement() {
//        multipartProperties.setEnabled(true);
//	multipartProperties.setMaxFileSize(ofMegabytes(20));
//        multipartProperties.setMaxRequestSize(ofMegabytes(20));
//        return multipartProperties.createMultipartConfig();
//    }
//
//    @Bean
//    @Order(0)
//    @Description("Multipart filter for file posting")
//    public MultipartFilter multipartFile() {
//        final MultipartFilter multipartFilter = new MultipartFilter();
//        // same name's request parameter => final @RequestParam MultipartFile file
//        multipartFilter.setMultipartResolverBeanName("file");
//        return multipartFilter;
//    }

//    @Bean
//    public CommonsMultipartResolver multipartResolver(){
//        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
////        commonsMultipartResolver.setMaxInMemorySize();
////        commonsMultipartResolver.setMaxUploadSize(ofMegabytes(20));
//        return commonsMultipartResolver;
//    }

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
