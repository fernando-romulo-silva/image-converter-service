package org.imageconverter.infra.config;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;

/**
 * @author Fernando Romulo da Silva
 *
 */
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
					.version(appVersion + " " + appVendor) //
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

	    // openapi spring boot programmatically example
//	    final var mySchema = new ObjectSchema();
//	    mySchema.name("MySchema");
//	    mySchema.addExample("""
//	    		{
//	    		  "timestamp": "2021-07-19T15:25:32.389836763",
//	    		  "status": 500,
//	    		  "error": "Internal Server Error",
//	    		  "message": "Unexpected error. Please, check the log with traceId and spanId for more detail",
//	    		  "traceId": "3d4144eeb01e3682",
//	    		  "spanId": "3d4144eeb01e3682"
//	    		}""");
//
//	    final var schemas = openApiCustomiser.getComponents().getSchemas();
//	    schemas.put(mySchema.getName(), mySchema);
	};
    }

    private void createResponse(final Operation operation, final RequestMethod requestMethod) {
	final var apiResponses = operation.getResponses();

	final var ex500 = """
			{
			    "timestamp": "1669564355551",
			    "status": 500,
			    "error": "Internal Server Error",
			    "message": "Unexpected error. Please, check the log with traceId and spanId for more detail",
			    "traceId": "3d4144eeb01e3682",
			    "spanId": "3d4144eeb01e3682"
			}""";

	final var ex401 = """
			{
			    "timestamp": 1669564355551,
			    "status": 401,
			    "error": "Unauthorized",
			    "message": "Unauthorized",
			    "path": "/rest/images/type/2"
			}""";

	final var ex403 = """
			{
			    "timestamp": 1669563774179,
			    "status": 403,
			    "error": "Forbidden",
			    "message": "Forbidden",
			    "path": "/rest/images/type"
			}""";

	final var httpStatusMap = Map.of( //
			INTERNAL_SERVER_ERROR, ex500, //
			UNAUTHORIZED, ex401, //
			FORBIDDEN, ex403 //
	);

	for (final var httpStatusEntrySet : httpStatusMap.entrySet()) {

	    final var httpStatus = httpStatusEntrySet.getKey();
	    final var httpStatusExample = httpStatusEntrySet.getValue();

	    apiResponses.computeIfAbsent( //
			    String.valueOf(httpStatus.value()), //
			    s -> {
				final var mediaType = new MediaType();
				mediaType.setExample(httpStatusExample);

				final var content = new Content();
				content.addMediaType(APPLICATION_JSON_VALUE, mediaType);
				
				return new ApiResponse() //
						.description(httpStatus.getReasonPhrase()) //
						.content(content);
			    });
	}
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

		operation.description("Get " + classEntity + methodEntity);
		operation.operationId("getItem");
		operation.summary("Get items bla bla");
		operation.addExtension("x-operationWeight", "200");

		return operation;
	    }

	    if (listAnnotationTypes.contains(PostMapping.class)) {

		final var methodEntity = getMethodRequestMapping(method, GetMapping.class);

		operation.description("Create " + classEntity + methodEntity);
		operation.operationId("createItem");
		operation.summary("Create items bla bla");
		operation.addExtension("x-operationWeight", "300");

		return operation;
	    }

	    if (listAnnotationTypes.contains(PutMapping.class)) {

		final var methodEntity = getMethodRequestMapping(method, PutMapping.class);

		operation.description("Update " + classEntity + methodEntity);
		operation.operationId("updateItem");
		operation.summary("Update items bla bla");
		operation.addExtension("x-operationWeight", "400");

		return operation;
	    }

	    if (listAnnotationTypes.contains(DeleteMapping.class)) {

		final var methodEntity = getMethodRequestMapping(method, PutMapping.class);

		operation.description("Delete " + classEntity + methodEntity);
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
