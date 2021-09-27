package org.imageconverter.config.security.adapter;

import org.imageconverter.config.filter.CsrfLoggerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

//@Configuration
//@Order(3)
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CsrfTokenRepository csrfTokenRepository;
//

//    @Autowired
//    public GenericSecurityConfig(final HttpFirewall allowUrlEncodedSlashHttpFirewall, final CsrfTokenRepository httpSessionCsrfTokenRepository) {
//	super(true); // disable default configuration
//	this.mvcTokenRepository = httpSessionCsrfTokenRepository;
//	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
//    }

    @Autowired
    public BasicSecurityConfig(final CsrfTokenRepository httpSessionCsrfTokenRepository) {
	super(true); // disable default configuration
	this.csrfTokenRepository = httpSessionCsrfTokenRepository;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

	http.addFilterAfter(new CsrfLoggerFilter(), CsrfFilter.class) //
			//
			.securityContext() //
			.and().csrf() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)//
			//
			.and().requestMatcher(EndpointRequest.toAnyEndpoint()) //
			/*------*/.authorizeRequests() //
			/*------------*/.anyRequest().hasRole("ADMIN")

	;
    }
}
