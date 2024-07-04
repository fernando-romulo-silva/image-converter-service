package org.imageconverter.infra.config.security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.imageconverter.infra.config.filter.CsrfLoggerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import static org.springframework.security.config.Customizer.withDefaults;

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
//		    final CsrfTokenRepository httpSessionCsrfTokenRepository
		    final CsrfTokenRepository cookieCsrfTokenRepository
		    ) {
	//
	this.authenticationSuccessHandler = authenticationSuccessHandler;
	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
	this.csrfTokenRepository = cookieCsrfTokenRepository;
//	this.csrfTokenRepository = httpSessionCsrfTokenRepository;
    }

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http) throws Exception { // NOPMD - Filter throw it

	final var restUrl = "/rest/**";

        final var authorizeRequestsCustomizer = extracted01(restUrl);
        
	http.addFilterAfter(new CsrfLoggerFilter(), CsrfFilter.class)
                .securityContext(withDefaults())
                .exceptionHandling(withDefaults())
                .servletApi(withDefaults())
                .httpBasic(withDefaults())
                
                .authorizeRequests(authorizeRequestsCustomizer)
                
                .formLogin(login -> login // disable redirect
                		.successHandler(authenticationSuccessHandler) //
                		.failureHandler(new SimpleUrlAuthenticationFailureHandler()))
                		.logout(logout -> logout
                				.logoutSuccessUrl("/")
                				.invalidateHttpSession(true)
                				.clearAuthentication(true))
                		
                .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository).ignoringAntMatchers("/actuator/**"));

        http.headers(headers -> headers.frameOptions().sameOrigin());

	return http.build();
    }

    private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> extracted01(final String restUrl) {
	return requests -> requests
			.antMatchers(swaggerUiURL).permitAll()
			.antMatchers(GET, restUrl).hasAnyRole("USER") // , "GUEST"
			.antMatchers(POST, restUrl).hasRole("USER") //
			//.antMatchers("/actuator/**").hasRole("ADMIN") 			
			.antMatchers(DELETE, restUrl).access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			.antMatchers(restUrl).hasAnyRole("ADMIN", "USER");
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
	return (web) -> {
	    web.httpFirewall(allowUrlEncodedSlashHttpFirewall);
	};
    }
}
