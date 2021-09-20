package org.imageconverter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sourceforge.tess4j.Tesseract;

@Configuration
//@EnableConfigurationProperties(MultipartProperties.class)
public class ImageConfig {

    private final String tesseractFolder;

    private final String tesseractLanguage;

    private final String tesseractDpi;

    ImageConfig( //
		    @Value("${tesseract.folder}") //
		    final String tesseractFolder, //
		    //
		    @Value("${tesseract.language}") //
		    final String tesseractLanguage,

		    @Value("${tesseract.dpi}") //
		    final String tesseractDpi

    ) {
	super();
	this.tesseractFolder = tesseractFolder;
	this.tesseractLanguage = tesseractLanguage;
	this.tesseractDpi = tesseractDpi;
    }

    @Bean
    @RefreshScope
    public Tesseract tesseractTess4j() {
	final var tesseract = new Tesseract();

	tesseract.setDatapath(tesseractFolder);
	tesseract.setLanguage(tesseractLanguage);
	tesseract.setTessVariable("user_defined_dpi", tesseractDpi);

	return tesseract;
    }

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
