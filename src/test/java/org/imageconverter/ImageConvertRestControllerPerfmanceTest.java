package org.imageconverter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.imageconverter.infra.util.controllers.imagetype.ImageTypeConst.REST_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Class to execute a performance test.
 * 
 * @author Fernando Romulo da Silva
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user") // application-test.yml-application.user_login: user
@Sql(scripts = "classpath:db/db-data-test.sql", config = @SqlConfig(errorMode = ErrorMode.CONTINUE_ON_ERROR))
//
@Disabled("It's not working yeat")
@Tag("performance")
@DisplayName("Test the performance's image type controller :0")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageConvertRestControllerPerfmanceTest {

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void beforeAll() {
	System.setProperty("junit.jupiter.execution.parallel.enabled", "true");
	System.setProperty("junit.jupiter.execution.parallel.config.strategy", "fixed");
	System.setProperty("junit.jupiter.execution.parallel.config.fixed.parallelism", "4");
    }

    /**
     * Execute the performance test.
     */
    @Order(1)
    @DisplayName("get a image type by id")
    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}") // Jconsole or visualVMS
    void performanceTest() throws Exception { // NOPMD

	mvc.perform(get(REST_URL) //
			.accept(MediaType.APPLICATION_JSON) //
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
