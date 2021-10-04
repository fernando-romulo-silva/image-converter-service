package org.imageconverter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.util.controllers.ImageTypeConst.REST_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.SqlConfig.ErrorMode.CONTINUE_ON_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.imageconverter.util.controllers.CreateImageTypeRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@Disabled("It's not working yeat")

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = CONTINUE_ON_ERROR))
//
@Tag("performance")
@DisplayName("Test the performance's image type controller :0")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageConvertRestControllerPerfmanceTest {

    @Autowired
    private MockMvc mvc;

    private CreateImageTypeRequest createImageTypeRequest = new CreateImageTypeRequest("BMP", "BitMap", "Device independent bitmap");
    
    @BeforeAll
    public static void beforeAll () {
	 System.setProperty("junit.jupiter.execution.parallel.enabled", "true");
	 System.setProperty("junit.jupiter.execution.parallel.config.strategy", "fixed");
	 System.setProperty("junit.jupiter.execution.parallel.config.fixed.parallelism", "4");
    }
    

    @Order(1)
    @DisplayName("get a image type by id")
    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}") // Jconsole or visualVMS    
    public void metho() throws Exception {

	mvc.perform(get(REST_URL) //
			.accept(APPLICATION_JSON) //
			.with(csrf())) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*].extension").value(containsInAnyOrder("png", "jpg"))) //
	;
	
	TimeUnit.SECONDS.sleep(2);

    }

}
