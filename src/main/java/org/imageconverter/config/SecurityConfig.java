package org.imageconverter.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// https://freecontent.manning.com/five-awkward-things-about-spring-security-that-actually-make-sense/

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Value("${application.user_login}")
    private String applicationUser;

    @Value("${application.user_password}")
    private String applicationPassword;

    public SecurityConfig() {
	super(true); // disable default configuration
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	registry.addViewController("/login.html").setViewName("login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

	http.securityContext() //
			.and().exceptionHandling() //
			.and().servletApi() //
			.and().httpBasic() //
			//
			.and().logout() //
			/*------*/.logoutSuccessUrl("/") //
			/*------*/.invalidateHttpSession(true)//
			/*------*/.clearAuthentication(true)//
			//
			.and().headers() //
			.and().csrf() //
			/*------*/.csrfTokenRepository(repo())
			//
			.and().anonymous() //
			/*-*/.principal("guest") //
			/*-*/.authorities("ROLE_GUEST") //
			//
			.and().rememberMe() //
			//
			.and().formLogin() //
			/*------*/.loginPage("/login.html") //
			/*------*/.defaultSuccessUrl("/") //
			/*------*/.failureUrl("/login.html?error=true") //
			/*------*/.permitAll() //
			//
			.and().authorizeRequests() //
			/*--*/.antMatchers(GET, "/rest/*") //
			/*------*/.hasAnyRole("USER", "GUEST") //
			//
			/*--*/.antMatchers(POST, "/rest/*") //
			/*------*/.hasRole("USER") //
			//
			/*--*/.antMatchers(DELETE, "/rest/*") //
			/*------*/.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			//
			/*--*/.mvcMatchers( //
					"/health/**", //
					"/v3/api-docs/**", //
					"/configuration/**", //
					"/swagger-resources/**", //
					"/swagger-ui.html", //
					"/swagger-ui/**", //
					"/webjars/**") //
			/*------*/.permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

	final var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	final var adminUser = User//
			.withUsername(applicationUser) //
			.password(encoder.encode(applicationPassword)) //
			.roles("USER", "ADMIN") //
			.build();

	auth.inMemoryAuthentication() //
			// .withUser(normalUser) //
			// .withUser(disabledUser) //
			.withUser(adminUser);
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	StrictHttpFirewall firewall = new StrictHttpFirewall();
	firewall.setAllowUrlEncodedSlash(true);
	return firewall;
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
	super.configure(web);
	web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public CsrfTokenRepository repo() {
	HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
	repo.setParameterName("_csrf");
	repo.setHeaderName("X-CSRF-TOKEN");
	return repo;
    }

    @Controller
    static class FaviconController {

	@GetMapping("favicon.ico")
	@ResponseBody
	void returnNoFavicon() {
	}
    }
}
