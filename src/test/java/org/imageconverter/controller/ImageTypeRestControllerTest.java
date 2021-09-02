package org.imageconverter.controller;

import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.imageconverter.util.controllers.ImageTypeConst.REST_URL;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = RANDOM_PORT)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc

//
@Tag("integration")
@DisplayName("Test the image type controller")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ImageTypeRestControllerTest {

    private static final String USER = "user"; // application-test.yml-application.user_login: user
    
    // JSqlParser
    
    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Value("classpath:db/db-data-test.sql")
    private Resource dbDataTest;

    @Autowired
    private MockMvc mvc;

//    @BeforeAll
//    public void beforeAll() {
//	mvc = MockMvcBuilders //
//			.webAppContextSetup(context) //
//			.apply(springSecurity()) //
//			.build();
//    }

    @Test
    @Order(1)
    @DisplayName("Create a new image type")
    @WithMockUser(username = USER)
    @Sql(statements = "DELETE FROM image_type")
    @Sql("classpath:db/db-data-test.sql")
    public void createImageTypeTest() throws Exception {

	mvc.perform(post(REST_URL) //
			.content("") //
			.contentType(APPLICATION_JSON) //
			.accept(TEXT_PLAIN, APPLICATION_JSON)) //
			.andDo(print()) //
			.andExpect(status().isCreated()) //
	;

	mvc.perform(get(REST_URL + "/{id}", 1) //
			.accept(APPLICATION_JSON)) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$.id").value(1)) //
	;

    }

    @Test
    @Order(2)
    @DisplayName("get all image types")
    @WithMockUser(username = USER)
    @Sql(statements = "DELETE FROM image_type")
    @Sql("classpath:db/db-data-test.sql")
    public void getAllImageTypeTest() throws Exception {

	mvc.perform(get(REST_URL + "/{id}", 1) //
			.accept(APPLICATION_JSON)) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*]").value(arrayContainingInAnyOrder(toArray("", ""))));
    }

    @Test
    @Order(2)
    @DisplayName("get a image type by id")
    @WithMockUser(username = USER)
    @Sql(statements = "DELETE FROM image_type")
    @Sql("classpath:db/db-data-test.sql")
    public void getImageTypeByIdTest() throws Exception {

	mvc.perform(get(REST_URL + "/{id}", 1) //
			.accept(APPLICATION_JSON)) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*]").value(arrayContainingInAnyOrder(toArray("", ""))));
    }

    // https://www.google.com/search?q=pattern+rest+api+get+element+by&oq=pattern+rest+api+get+element+by&aqs=chrome..69i57j33i22i29i30.14776j1j9&sourceid=chrome&ie=UTF-8
    // https://blog.stoplight.io/api-design-patterns-for-rest-web-services
    //
    // https://www.google.com/search?q=spring+boot+rest+api+filtering+&sxsrf=ALeKk02mIr4UYHLJKXeA_I8c10XKydCwkw%3A1627857728567&ei=QCMHYcuAIrjI1sQPju2jKA&oq=spring+boot+rest+api+filtering+&gs_lcp=Cgdnd3Mtd2l6EAMyBAgjECcyBggAEBYQHjIGCAAQFhAeMgYIABAWEB4yBggAEBYQHjIGCAAQFhAeMgYIABAWEB46BwgAEEcQsANKBAhBGABQsMoIWLDKCGCOzQhoA3ACeACAAWeIAcQBkgEDMS4xmAEAoAEByAEIwAEB&sclient=gws-wiz&ved=0ahUKEwjLoJ_N8pDyAhU4pJUCHY72CAUQ4dUDCA4&uact=5
    // https://stackoverflow.com/questions/42087885/how-do-i-filter-data-in-a-restful-way-using-spring
    //
    // https://github.com/jirutka/rsql-parser
    // https://www.baeldung.com/rest-api-search-language-spring-data-specifications
    // https://medium.com/quick-code/spring-boot-how-to-design-efficient-search-rest-api-c3a678b693a0#id_token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjBmY2MwMTRmMjI5MzRlNDc0ODBkYWYxMDdhMzQwYzIyYmQyNjJiNmMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2Mjc4NTgwMjMsImF1ZCI6IjIxNjI5NjAzNTgzNC1rMWs2cWUwNjBzMnRwMmEyamFtNGxqZGNtczAwc3R0Zy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMzE1NzQxNTU3MTQzMTM0MjQ3NyIsImVtYWlsIjoiZmVybmFuZG8ucm9tdWxvLnNpbHZhQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiIyMTYyOTYwMzU4MzQtazFrNnFlMDYwczJ0cDJhMmphbTRsamRjbXMwMHN0dGcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJuYW1lIjoiRmVybmFuZG8gUm9tdWxvIGRhIFNpbHZhIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdndnNvTS0zdWlvOVZDcmZ2bDVVM3RrNTI3YktZcE9qZnAycDEyNWJRPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkZlcm5hbmRvIiwiZmFtaWx5X25hbWUiOiJSb211bG8gZGEgU2lsdmEiLCJpYXQiOjE2Mjc4NTgzMjMsImV4cCI6MTYyNzg2MTkyMywianRpIjoiMTU5ZDI4NDgwOTFiYjA0NGE3MTA1NjA2MzcxZmVlYjc2ZjdmMzk4NyJ9.D_bgUNDO6mGZa5cp9-_JT1ajqqXPuB7lZ6NPaczkY6559Z-gQInE90MkCgMwMrtZXJkPFyyNKrQLJfzyQ6UKD0IQM80DpHrUnrLdTdQmVa1-kEH1DlfkauQJGyfqIZZJkmgRfjm8o5ZwVZG_73NAURUeL1jYouRzmEdghnk0sI7JjGXenHIRRU7B98D54qAPRaH-27y6m7C2UYPLtkbGZfn-QZD0vCvbRmYpYsP0EBAR2-TNIxC3H0Y4oQOWMEWJIQWEVeRtUVuA0vTJxRIL0AZtuUoGPGRznBaymEf3-s7vqOcvuUL2ozTKkD5wKbEoffmdhjkO3BOJPVKp4cziyg
    // https://www.baeldung.com/rest-api-search-language-rsql-fiql
    //
    @Test
    @Order(3)
    @DisplayName("get a image type by extension")
    @WithMockUser(username = USER)
    @Sql(statements = "DELETE FROM image_type")
    @Sql("classpath:db/db-data-test.sql")
    public void getImageTypeByExtensionTest() throws Exception {

	mvc.perform(get(REST_URL + "?criteria=", 1) //
			.accept(APPLICATION_JSON)) //
			.andDo(print()) //
			.andExpect(status().isOk()) //
			.andExpect(jsonPath("$").exists()) //
			.andExpect(jsonPath("$").isArray()) //
			.andExpect(jsonPath("$[*]").value(arrayContainingInAnyOrder(toArray("", ""))));
    }

}
