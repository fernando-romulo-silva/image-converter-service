package org.imageconverter.config.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter created to pass the CSRF token on response.
 * 
 * @author Fernando Romulo da Silva
 */
public final class CsrfLoggerFilter extends OncePerRequestFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

	final var csrfToken = (CsrfToken) request.getAttribute("_csrf");

	if (Objects.nonNull(csrfToken)) {
	    response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());
	}

	filterChain.doFilter(request, response);
    }
}
