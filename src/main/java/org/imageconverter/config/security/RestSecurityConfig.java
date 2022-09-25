package org.imageconverter.config.security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.imageconverter.config.filter.CsrfLoggerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;

// https://freecontent.manning.com/five-awkward-things-about-spring-security-that-actually-make-sense/

/**
 * Project http's config.
 * 
 * @author Fernando Romulo da Silva
 */
@Configuration
public class RestSecurityConfig {

    private final String[] swaggerUiURL = { //
	    "/v3/api-docs/**", //
	    "/swagger-ui/**", //
	    "/swagger-ui.html", //
	    "/webjars/**" //
    };

    private final RestAuthenticationSuccessHandler authenticationSuccessHandler;

    private final HttpFirewall allowUrlEncodedSlashHttpFirewall;

    private final CsrfTokenRepository csrfTokenRepository;

    RestSecurityConfig( //
		    final RestAuthenticationSuccessHandler authenticationSuccessHandler, //
		    final HttpFirewall allowUrlEncodedSlashHttpFirewall, //
		    final CsrfTokenRepository httpSessionCsrfTokenRepository
//		    final CsrfTokenRepository cookieCsrfTokenRepository
		    ) {
	//
	this.authenticationSuccessHandler = authenticationSuccessHandler;
	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
//	this.csrfTokenRepository = cookieCsrfTokenRepository;
	this.csrfTokenRepository = httpSessionCsrfTokenRepository;
    }

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {

	final var restUrl = "/rest/**";

	http.addFilterAfter(new CsrfLoggerFilter(), CsrfFilter.class) //
			.securityContext() //
			.and().exceptionHandling() //
			.and().servletApi() //
			.and().httpBasic() //
			//
			.and().authorizeRequests() //
			//
			/*--*/.antMatchers(swaggerUiURL) //
			/*------*/.permitAll()
			//
			/*--*/.antMatchers(GET, restUrl) // /rest/images/type
			/*------*/.hasAnyRole("USER") // , "GUEST"
			//
			/*--*/.antMatchers(POST, restUrl) //
			/*------*/.hasRole("USER") //
			//
//			/*--*/.antMatchers("/actuator/**")
//			/*------*/.hasRole("ADMIN") 			
			//
			/*--*/.antMatchers(DELETE, restUrl) //
			/*------*/.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			//
			/*--*/.antMatchers(restUrl) //
			/*------*/.hasAnyRole("ADMIN", "USER")
			//
			.and().formLogin() // disable redirect
			/*------*/.successHandler(authenticationSuccessHandler) //
			/*------*/.failureHandler(new SimpleUrlAuthenticationFailureHandler()) //
			//
			.and().logout() //
			/*------*/.logoutSuccessUrl("/") //
			/*------*/.invalidateHttpSession(true)//
			/*------*/.clearAuthentication(true)//
			//
			.and().csrf() //
//			/*------*/.disable() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)//
			/*------*/.ignoringAntMatchers("/actuator/**")
			;

	http.headers().frameOptions().sameOrigin();

	return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
	return (web) -> {
	    web.httpFirewall(allowUrlEncodedSlashHttpFirewall);
	};
    }
}
