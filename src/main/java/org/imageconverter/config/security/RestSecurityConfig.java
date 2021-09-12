package org.imageconverter.config.security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;

// https://freecontent.manning.com/five-awkward-things-about-spring-security-that-actually-make-sense/

@Order(1)
@Configuration
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${application.user_login}")
    private String applicationUser;

    @Value("${application.user_password}")
    private String applicationPassword;

    private final RestAuthenticationSuccessHandler authenticationSuccessHandler;

    private final CsrfTokenRepository csrfTokenRepository;

    private final HttpFirewall allowUrlEncodedSlashHttpFirewall;

    @Autowired
    public RestSecurityConfig(final RestAuthenticationSuccessHandler authenticationSuccessHandler, final HttpFirewall allowUrlEncodedSlashHttpFirewall, final CsrfTokenRepository csrfTokenRepository) {
	super(true); // disable default configuration
	this.authenticationSuccessHandler = authenticationSuccessHandler;
	this.csrfTokenRepository = csrfTokenRepository;
	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

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
// https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/csrf.html			
// https://docs.spring.io/spring-security/site/docs/current/reference/html5/#jc-httpsecurity
// https://stackoverflow.com/questions/49486675/how-to-make-multipartfilter-to-work-with-spring-boot
			//
			.and().csrf() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)
			//
			.and().authorizeRequests() //
			/*--*/.antMatchers(GET, "/rest/**") // /rest/images/type
			/*------*/.hasAnyRole("USER") // , "GUEST"
			//
			/*--*/.antMatchers(POST, "/rest/**") //
			/*------*/.hasRole("USER") //
			//
			/*--*/.antMatchers(DELETE, "/rest/*") //
			/*------*/.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			//
			/*--*/.antMatchers( //
					"/health/**", //
					"/v3/api-docs/**", //
					"/configuration/**", //
					"/swagger-resources/**", //
					"/swagger-ui.html", //
					"/swagger-ui/**", //
					"/webjars/**") //
			/*------*/.permitAll() //
//			//
			.and().formLogin() // disable redirect
			/*------*/.successHandler(authenticationSuccessHandler) //
			/*------*/.failureHandler(new SimpleUrlAuthenticationFailureHandler()) //

	;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

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

    @Override
    public void configure(final WebSecurity web) throws Exception {
	super.configure(web);
	web.httpFirewall(allowUrlEncodedSlashHttpFirewall);
    }
}
