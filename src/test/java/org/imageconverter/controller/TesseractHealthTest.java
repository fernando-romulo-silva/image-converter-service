package org.imageconverter.controller;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfTokenRepository;

public class TesseractHealthTest {

    @Value("${server.port}")
    protected int serverPort;

    @Autowired
    protected CsrfTokenRepository httpSessionCsrfTokenRepository;
    
    protected HttpHeaders basicAuthHeaders() {
	final var plainCreds = "user:password"; // application-test.yml-application.user_login: user
	final var plainCredsBytes = plainCreds.getBytes();
	final var base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
	final var base64Creds = new String(base64CredsBytes);

	final var headers = new HttpHeaders();
	headers.add("Authorization", "Basic " + base64Creds);
	return headers;
    }

    protected HttpHeaders csrfHeaders() {
	final var csrfToken = httpSessionCsrfTokenRepository.generateToken(null);
	final var headers = basicAuthHeaders();
	
	headers.set("Content-Type", "application/json");
	headers.add(csrfToken.getHeaderName(), csrfToken.getToken());
	headers.add("Cookie", "XSRF-TOKEN=" + csrfToken.getToken());

	return headers;
    }
}
