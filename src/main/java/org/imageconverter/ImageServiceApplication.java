package org.imageconverter;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition( //
		info = @Info( //
				title = "Image Converter API", //
				description = "This API allows convert image to text", //
				version = "1.0.0", //
				contact = @Contact( //
						name = "Fernando Romulo da Silva", //
						url = "https://github.com/fernando-romulo-silva/" //
				), //
				license = @License( //
						name = "Apache 2.0", //
						url = "https://opensource.org/licenses/Apache-2.0"//
				)//
		), //
		externalDocs = @ExternalDocumentation( //
				url = "https://github.com/fernando-romulo-silva/image-converter-service#readme", //
				description = "Project's Documentation" //
		) //
//		servers = @Server( //
//				description = "Vintage Store server 1", //
//				url = "http://{host}.vintage-store/{port}", //
//				variables = { //
//					@ServerVariable(name = "host", description = "Vintage Store main server", defaultValue = "localhost"), //
//					@ServerVariable(name = "port", description = "Vintage Store listening port", defaultValue = "80") //
//				} //
//		) //
)
@SecurityScheme( //
		name = "BASIC", //
		scheme = "basic", //
		type = SecuritySchemeType.HTTP, //
		in = SecuritySchemeIn.HEADER //
)
//
@SpringBootApplication
public class ImageServiceApplication { // NOPMD - It's not a util class, it's a starter

    public static void main(final String[] args) {

	run(ImageServiceApplication.class, args);
    }
}
