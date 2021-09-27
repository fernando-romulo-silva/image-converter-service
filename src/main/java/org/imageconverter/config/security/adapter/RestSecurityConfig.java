package org.imageconverter.config.security.adapter;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.imageconverter.config.filter.CsrfLoggerFilter;
import org.imageconverter.config.security.RestAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;

// https://freecontent.manning.com/five-awkward-things-about-spring-security-that-actually-make-sense/

@Configuration
@Order(1)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationSuccessHandler authenticationSuccessHandler;

    private final HttpFirewall allowUrlEncodedSlashHttpFirewall;

    private final CsrfTokenRepository csrfTokenRepository;
    
    @Autowired
    public RestSecurityConfig(final RestAuthenticationSuccessHandler authenticationSuccessHandler, final HttpFirewall allowUrlEncodedSlashHttpFirewall, final CsrfTokenRepository httpSessionCsrfTokenRepository) {
	super(true); // disable default configuration
	this.authenticationSuccessHandler = authenticationSuccessHandler;
	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
	this.csrfTokenRepository = httpSessionCsrfTokenRepository;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

	http.addFilterAfter(new CsrfLoggerFilter(), CsrfFilter.class) //
			.securityContext() //
			.and().exceptionHandling() //
			.and().servletApi() //
			.and().httpBasic() //
			//
			.and().authorizeRequests() //
			/*--*/.antMatchers(GET, "/rest/**") // /rest/images/type
			/*------*/.hasAnyRole("USER") // , "GUEST"
			//
			/*--*/.antMatchers(POST, "/rest/**") //
			/*------*/.hasRole("USER") //
			//
			/*--*/.antMatchers(DELETE, "/rest/**") //
			/*------*/.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			//
			/*--*/.antMatchers("/rest/**") //
			/*------*/.hasAnyRole("ADMIN", "USER")
			//
			/*--*/.antMatchers( //
//					"/health/**", //
//					"/actuator/**", //
					"/v3/api-docs/**", //
					"/configuration/**", //
					"/swagger-resources/**", //
					"/swagger-ui.html", //
					"/swagger-ui/**", //
					"/webjars/**") //
			/*------*/.permitAll() //
			//
			.and().csrf() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)//
//			/*------*/.ignoringAntMatchers("/actuator/**")				

			.and().formLogin() // disable redirect
			/*------*/.successHandler(authenticationSuccessHandler) //
			/*------*/.failureHandler(new SimpleUrlAuthenticationFailureHandler()) //
			//
			.and().logout() //
			/*------*/.logoutSuccessUrl("/") //
			/*------*/.invalidateHttpSession(true)//
			/*------*/.clearAuthentication(true)//
	;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
	super.configure(web);
	web.httpFirewall(allowUrlEncodedSlashHttpFirewall);
    }
}
