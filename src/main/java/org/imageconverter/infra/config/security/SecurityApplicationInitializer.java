package org.imageconverter.infra.config.security;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.multipart.support.MultipartFilter;

/**
 * Insert a {@link MultipartFilter} to filter's chain.
 * 
 * @author Fernando Romulo da Silva
 */
public class SecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeSpringSecurityFilterChain(final ServletContext servletContext) {
	insertFilters(servletContext, new MultipartFilter());
    }
}
