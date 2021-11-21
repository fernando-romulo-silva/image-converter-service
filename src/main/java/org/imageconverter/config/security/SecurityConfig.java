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

@Configuration
public class SecurityConfig {

    @Value("${application.user_login}")
    private String applicationUser;

    @Value("${application.user_password}")
    private String applicationPassword;

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	StrictHttpFirewall firewall = new StrictHttpFirewall();
	firewall.setAllowUrlEncodedSlash(true);
	return firewall;
    }

    // https://www.baeldung.com/csrf-thymeleaf-with-spring-security
    @Bean
    public CsrfTokenRepository httpSessionCsrfTokenRepository() {

	final var repo = new HttpSessionCsrfTokenRepository(); // session
	repo.setParameterName("_csrf");
	repo.setHeaderName("X-CSRF-TOKEN");
	return repo;

    }

    @Bean
    public CsrfTokenRepository cookieCsrfTokenRepository() {

	return CookieCsrfTokenRepository.withHttpOnlyFalse(); // cookie
    }

    @Bean
    public UserDetailsService userDetailsService() throws Exception {

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

    @Controller
    public static class FaviconController {

	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {
	    // just to fix favicon.ico problem
	}
    }
}
