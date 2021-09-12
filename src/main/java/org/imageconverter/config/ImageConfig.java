package org.imageconverter.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableConfigurationProperties(MultipartProperties.class)
public class ImageConfig {

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
