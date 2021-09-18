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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(2)
@Configuration
public class MvcSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Value("${application.user_login}")
    private String applicationUser;

    @Value("${application.user_password}")
    private String applicationPassword;

    private final HttpFirewall allowUrlEncodedSlashHttpFirewall;

    private final CsrfTokenRepository csrfTokenRepository;

    @Autowired
    public MvcSecurityConfig(final HttpFirewall allowUrlEncodedSlashHttpFirewall, final CsrfTokenRepository csrfTokenRepository) {
	super(true); // disable default configuration
	this.allowUrlEncodedSlashHttpFirewall = allowUrlEncodedSlashHttpFirewall;
	this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
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
			.and().csrf() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)//
			//
			.and().anonymous() //
			/*-*/.principal("guest") //
			/*-*/.authorities("ROLE_GUEST") //
			//
			.and().rememberMe() //
			//
			.and().authorizeRequests() //
			/*--*/.mvcMatchers(GET, "/mvc/**") // /rest/images/type
			/*------*/.hasAnyRole("USER") // , "GUEST"
			//
			/*--*/.mvcMatchers(POST, "/mvc/**") //
			/*------*/.hasRole("USER") //
			//
			/*--*/.mvcMatchers(DELETE, "/mvc/**") //
			/*------*/.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") //
			//
			//
			/*--*/.mvcMatchers( //
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
			.and().formLogin() //
			/*------*/.loginPage("/login.html") //
			/*------*/.defaultSuccessUrl("/") //
			/*------*/.failureUrl("/login.html?error=true") //
			/*------*/.permitAll() //

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
