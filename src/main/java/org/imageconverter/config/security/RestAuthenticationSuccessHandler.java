package org.imageconverter.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {

	final var savedRequest = requestCache.getRequest(request, response);

	if (savedRequest == null) {
	    clearAuthenticationAttributes(request);
	    return;
	}

	final var targetUrlParam = getTargetUrlParameter();

	if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
	    requestCache.removeRequest(request, response);
	    clearAuthenticationAttributes(request);
	    return;
	}

	clearAuthenticationAttributes(request);
    }

    public void setRequestCache(final RequestCache requestCache) {
	this.requestCache = requestCache;
    }
}
