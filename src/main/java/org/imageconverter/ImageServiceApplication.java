package org.imageconverter;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition( //
		info = @Info( //
				title = "Image Converter API", //
				description = "This API allows convert image to text", //
				version = "1.0", //
				contact = @Contact(name = "Fernando Romulo da Silva", url = "https://github.com/fernando-romulo-silva/"), //
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
@SpringBootApplication
public class ImageServiceApplication {

    public static void main(final String[] args) {
	run(ImageServiceApplication.class, args);
    }
}
