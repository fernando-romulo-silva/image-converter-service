package org.imageconverter.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// https://freecontent.manning.com/five-awkward-things-about-spring-security-that-actually-make-sense/

/**
 * Project security's config.
 * 
 * @author Fernando Romulo da Silva
 */
@Configuration
public class SecurityConfig {

    private final String applicationUser;

    private final String applicationPassword;

    /**
     * Default constructor.
     * 
     * @param applicationUser     Default application user login
     * @param applicationPassword Default application user password
     */
    SecurityConfig( //
		    @Value("${application.user_login}") //
		    final String applicationUser, //
		    //
		    @Value("${application.user_password}") //
		    final String applicationPassword) {
	super();
	this.applicationUser = applicationUser;
	this.applicationPassword = applicationPassword;
    }

    /**
     * Create a configured filter.
     * 
     * @return a {@link HttpFirewall} configured
     */
    @Bean
    HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	final var firewall = new StrictHttpFirewall();
	firewall.setAllowUrlEncodedSlash(true);
	firewall.setAllowSemicolon(true);
	return firewall;
    }

    // https://www.baeldung.com/csrf-thymeleaf-with-spring-security
    /**
     * Create a session csrf token repository.
     * 
     * @return a {@link CsrfTokenRepository} configured
     */
    @Bean
    CsrfTokenRepository httpSessionCsrfTokenRepository() {
	final var repo = new HttpSessionCsrfTokenRepository(); // session
//	repo.setParameterName("_csrf");
//	repo.setHeaderName("X-CSRF-TOKEN");
	return repo;

    }

    /**
     * Create a cookie csrf token repository.
     * 
     * @return a {@link CsrfTokenRepository} configured
     */
    @Bean
    CsrfTokenRepository cookieCsrfTokenRepository() {
        final var repo = CookieCsrfTokenRepository.withHttpOnlyFalse(); // cookie
        // X-XSRF-TOKEN
//        repo.setHeaderName("X-XSRF-TOKEN");
        return repo;	
    }

    /**
     * Create a user recorer.
     * 
     * @return a {@link UserDetailsService} configured
     */
    @Bean
    UserDetailsService userDetailsService() {

	final var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	final var manager = new InMemoryUserDetailsManager();

	final var adminUser = User//
			.withUsername(applicationUser) //
			.password(encoder.encode(applicationPassword)) //
			.roles("USER", "ADMIN") //
			.build();

	manager.createUser(adminUser);

	return manager;
    }

//    @Bean
//    public PasswordEncoder encoder(){
//        return new BCryptPasswordEncoder();
//    }

    /**
     * Fix the Favicon problem.
     * 
     * @author Fernando Romulo da Silva
     */
    @Controller
    public static class FaviconController {

	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {
	    // just to fix favicon.ico problem
	}
    }
}
