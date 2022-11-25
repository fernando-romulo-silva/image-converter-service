package org.imageconverter.config;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class DefaultOpenApiConfiguration {

    @Value("${spring.application.name:Please set the project's name ('spring.application.name')}")
    private String appName;

    @Value("${spring.application.description:Please set the project's name ('spring.application.description')}")
    private String appDesciption;

    public OpenAPI openAPI() {
	final var appVersion = getClass().getPackage().getImplementationVersion();
	final var appVendor = getClass().getPackage().getSpecificationVendor();

	return new OpenAPI() //
			.info(new Info() //
					.title(appName) //
					.version(appVersion) //
					.description(appDesciption) //
					.contact(new Contact().name("Fernando Romulo da Silva")).termsOfService("http://swagger.io/terms/") //
					.license(new License().name("Apache 2.0").url("http://springdoc.org")) //
			);
    }
    
//    @Bean
//    public GroupedOpenApi internalGroupedOpenApi(OpenApiCustomiser apiFromResourceCustomizer) {
//        return GroupedOpenApi.builder()
//                .setGroup("testGroup")
//                .pathsToMatch("/nothingreally")
//                .addOpenApiCustomiser(apiFromResourceCustomizer)
//                .build();
//    }

    @Bean
    OpenApiCustomiser defaultOpenApiCustomiser() {
	return openApiCustomiser -> {
	    
	    // openapi spring boot programmatically example
            final var mySchema = new ObjectSchema();
            mySchema.name("MySchema");
            mySchema.addExample(
        		    """
{
  "timestamp": "2021-07-19T15:25:32.389836763",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unexpected error. Please, check the log with traceId and spanId for more detail",
  "traceId": "3d4144eeb01e3682",
  "spanId": "3d4144eeb01e3682"
}        		    				
        		    				
        		    				"""
        		    );

            final var schemas = openApiCustomiser.getComponents().getSchemas();
            schemas.put(mySchema.getName() , mySchema);
	    
	    
            
            
	    
	    openApiCustomiser.getPaths().values().forEach(valuePath -> {

		final var getOperation = valuePath.getGet();
		if (Objects.nonNull(getOperation)) {
		    createResponse(getOperation, RequestMethod.GET);
		}

		final var postOperation = valuePath.getPost();
		if (Objects.nonNull(postOperation)) {
		    createResponse(postOperation, RequestMethod.POST);
		}

		final var putOperation = valuePath.getPut();
		if (Objects.nonNull(putOperation)) {
		    createResponse(putOperation, RequestMethod.PUT);
		}

		final var deleteOperation = valuePath.getDelete();
		if (Objects.nonNull(deleteOperation)) {
		    createResponse(deleteOperation, RequestMethod.DELETE);
		}

		final var headOperation = valuePath.getHead();
		if (Objects.nonNull(headOperation)) {
		    createResponse(headOperation, RequestMethod.HEAD);
		}

		final var optionOperation = valuePath.getOptions();
		if (Objects.nonNull(optionOperation)) {
		    createResponse(optionOperation, RequestMethod.OPTIONS);
		}
	    });
	};
    }

    private void createResponse(final Operation operation, final RequestMethod requestMethod) {
	final var apiResponses = operation.getResponses();

	final var httpStatusArray = List.of(INTERNAL_SERVER_ERROR, UNAUTHORIZED, FORBIDDEN);

	for (final var httpStatus : httpStatusArray) {

	    apiResponses.computeIfAbsent( //
			    String.valueOf(httpStatus.value()), //
			    s -> new ApiResponse() //
					    .description(httpStatus.getReasonPhrase()) //
					    .content(new Content().addMediaType("ffff", new MediaType())

					    ));
	}

//	// http 500: Internal Server Error ---------------------------------------------------------------
//	apiResponses.computeIfAbsent( //
//			String.valueOf(INTERNAL_SERVER_ERROR.value()), //
//			s -> new ApiResponse() //
//					.description(INTERNAL_SERVER_ERROR.getReasonPhrase()) //
//					.content(new Content()
//
//					));
//
//	// http 401: Unauthorized ------------------------------------------------------------------------
//	apiResponses.computeIfAbsent( //
//			String.valueOf(UNAUTHORIZED.value()), //
//			s -> new ApiResponse() //
//					.description(UNAUTHORIZED.getReasonPhrase()) //
//					.content(new Content()
//
//					));
//
//	// http 403: FORBIDDEN ------------------------------------------------------------------------
//	apiResponses.computeIfAbsent( //
//			String.valueOf(FORBIDDEN.value()), //
//			s -> new ApiResponse() //
//					.description(FORBIDDEN.getReasonPhrase()) //
//					.content(new Content()
//
//					));

    }
    
//    private PathItem addExamples(PathItem pathItem) {
//	    if(pathItem.getPost() !=null)  {
//	        //Note you can also Do this to APIResponses to insert info from a file into examples in say, a 200 response.
//	            pathItem.getPost().getRequestBody().getContent().values().stream()
//	                    .forEach(c ->{
//	                        String fileName = c.getExample().toString().replaceFirst("@","");
//	                        ObjectNode node = null;
//	                        try {
//	                            //load file from where you want. also don't insert is as a string, it wont format properly
//	                            node = (ObjectNode) new ObjectMapper().readTree(methodToReadInFileToString(fileName)); 
//	                        } catch (JsonProcessingException e) {
//	                            throw new RuntimeException(e);
//	                        }
//	                        c.setExample(node);
//	                    }
//	            );
//	    }
//	    return pathItem;
//	}
    
    @Bean
    OperationCustomizer operationCustomizer() {
	return (operation, handleMethod) -> {

	    if (StringUtils.isNotBlank(operation.getDescription()) || StringUtils.isNotBlank(operation.getSummary())) {
		return operation;
	    }

	    final var method = handleMethod.getMethod();

	    final var classEntity = getClassRequestMapping(method);
	    
	    final var listAnnotationTypes = Stream.of(method.getAnnotations()) //
			    .map(a -> a.annotationType()) //
			    .toList();
	    
	    if (listAnnotationTypes.contains(GetMapping.class)) { 
		
		final var methodEntity = getMethodRequestMapping(method, GetMapping.class);
		
		operation.description("Get "+ classEntity + methodEntity);
		operation.operationId("getItem");
		operation.summary("Get items bla bla");
		operation.addExtension("x-operationWeight", "200");
		
		return operation;
	    }
	    
	    if (listAnnotationTypes.contains(PostMapping.class)) { 
		
		final var methodEntity = getMethodRequestMapping(method, GetMapping.class);
		
		operation.description("Create "+ classEntity + methodEntity);
		operation.operationId("createItem");
		operation.summary("Create items bla bla");
		operation.addExtension("x-operationWeight", "300");
		
		return operation;
	    }
	    
	    if (listAnnotationTypes.contains(PutMapping.class)) { 
		
		final var methodEntity = getMethodRequestMapping(method, PutMapping.class);
		
		operation.description("Update "+ classEntity + methodEntity);
		operation.operationId("updateItem");
		operation.summary("Update items bla bla");
		operation.addExtension("x-operationWeight", "400");
		
		return operation;
	    }
	    
	    if (listAnnotationTypes.contains(DeleteMapping.class)) { 
		
		final var methodEntity = getMethodRequestMapping(method, PutMapping.class);
		
		operation.description("Delete "+ classEntity + methodEntity);
		operation.operationId("deleteItem");
		operation.summary("Delete items bla bla");
		operation.addExtension("x-operationWeight", "500");
		
		return operation;
	    }
	    

	    return operation;
	};
    }

    private String getClassRequestMapping(final Method method) {

	final var requestMappingAnnotationClass = Stream.of(method.getDeclaringClass().getAnnotations()) //
			.filter(p -> Objects.equals(p.annotationType(), RequestMapping.class)) //
			.findFirst() //
			.orElse(null);

	if (Objects.nonNull(requestMappingAnnotationClass)) {

	    final var values = (String[]) AnnotationUtils.getValue(requestMappingAnnotationClass, "value");

	    if (!ArrayUtils.isEmpty(values)) {

		return Stream.of(StringUtils.split(values[0], "/")) //
				.filter(s -> !StringUtils.containsAny(s, "{}")) //
				.collect(Collectors.joining("'s"));
	    }
	}

	return StringUtils.EMPTY;
    }

    private String getMethodRequestMapping(final Method method, final Class<? extends Annotation> clazz) {

	final var requestMappingAnnotationClass = Stream.of(method.getDeclaringClass().getAnnotations()) //
			.filter(p -> Objects.equals(p.annotationType(), clazz)) //
			.findFirst() //
			.orElse(null);

	if (Objects.nonNull(requestMappingAnnotationClass)) {

	    final var values = (String[]) AnnotationUtils.getValue(requestMappingAnnotationClass, "value");

	    if (!ArrayUtils.isEmpty(values)) {

		return Stream.of(StringUtils.split(values[0], "/")) //
				.filter(s -> !StringUtils.containsAny(s, "{}")) //
				.reduce((first, second) -> second) //
				.map(m -> "'s " + m) //
				.orElse(StringUtils.EMPTY);
	    }
	}

	return StringUtils.EMPTY;
    }

}
