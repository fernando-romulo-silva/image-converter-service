package org.imageconverter.controller.actuator;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * Base class to test with csf.
 * 
 * @author Fernando Romulo da Silva
 */
public class BaseTesseractHealthTest {

    @LocalServerPort
    protected int serverPort;

    @LocalManagementPort
    protected int managementPort;

    @Autowired
    protected CsrfTokenRepository httpSessionCsrfTokenRepository;
    
    /**
     * Create headers with basic authentication
     * 
     * @return A HttpHeaders object
     * @throws UnsupportedEncodingException If something get wrong
     */
    protected HttpHeaders basicAuthHeaders() throws UnsupportedEncodingException {
	final var plainCreds = "user:password"; // application-test.yml-application.user_login: user
	final var plainCredsBytes = plainCreds.getBytes("UTF-8");
	final var base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	final var base64Creds = new String(base64CredsBytes, "UTF-8");

	final var headers = new HttpHeaders();
	headers.add("Authorization", "Basic " + base64Creds);
	return headers;
    }

    /**
     * Create headers with basic authentication with csrf
     * 
     * @return A HttpHeaders object
     * @throws UnsupportedEncodingException If something get wrong
     */
    protected HttpHeaders csrfHeaders() throws UnsupportedEncodingException {

	final var csrfToken = httpSessionCsrfTokenRepository.generateToken(null);
	final var headers = basicAuthHeaders();

	headers.set("Content-Type", "application/json");
	headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
	headers.add("Cookie", "XSRF-TOKEN=" + csrfToken.getToken());

	return headers;
    }
}
