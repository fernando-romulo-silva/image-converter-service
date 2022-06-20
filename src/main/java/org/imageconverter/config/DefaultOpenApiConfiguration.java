package org.imageconverter.config;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
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

    public OpenApiCustomiser defaultOpenApiCustomiser() {
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
	};
    }

    private void createResponse(final Operation operation, final RequestMethod requestMethod) {
	final var apiResponses = operation.getResponses();

	apiResponses.computeIfAbsent( //
			String.valueOf(INTERNAL_SERVER_ERROR.value()), //
			s -> new ApiResponse() //
					.description("Unexpected error") //
					.content(new Content()
							
							)
	);
    }
}
