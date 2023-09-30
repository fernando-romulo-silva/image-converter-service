package org.imageconverter.infra.config.security;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

/**
 * Handler to clear saved cache, just for security.
 * 
 * @author Fernando Romulo da Silva
 */
@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
	
	final var requestCache = new HttpSessionRequestCache();

	final var savedRequest = requestCache.getRequest(request, response);

	if (isNull(savedRequest)) {
	    clearAuthenticationAttributes(request);
	    return;
	}

	final var targetUrlParam = getTargetUrlParameter();

	if (isAlwaysUseDefaultTargetUrl() || nonNull(targetUrlParam) && hasText(request.getParameter(targetUrlParam))) {

	    requestCache.removeRequest(request, response);
	    clearAuthenticationAttributes(request);

	} else {
	    clearAuthenticationAttributes(request);
	}
    }

//    public void setRequestCache(final RequestCache requestCache) {
//	this.requestCache = requestCache;
//    }
}
