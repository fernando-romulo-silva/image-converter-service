package org.imageconverter.controller;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test the {@link ImageTypeRestController} controller on unhappy path, only base feature.
 * 
 * @author Fernando Romulo da Silva
 */
class ImageTypeRestControllerUnHappyPathBaseTest {

    protected final ObjectMapper mapper;
    protected final MockMvc mvc;

    ImageTypeRestControllerUnHappyPathBaseTest(final ObjectMapper mapper, final MockMvc mvc) {
	super();
	this.mapper = mapper;
	this.mvc = mvc;
    }

    String asJsonString(final Object object) {

	try {
	    return mapper.writeValueAsString(object);
	} catch (final JsonProcessingException e) {
	    throw new IllegalStateException(e);
	}
    }
}