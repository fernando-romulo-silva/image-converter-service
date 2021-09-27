package org.imageconverter.config.security.adapter;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(2) // have to the last
public class MvcSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final CsrfTokenRepository csrfTokenRepository;

    @Autowired
    public MvcSecurityConfig(final CsrfTokenRepository cookieCsrfTokenRepository) {
	super(true); // disable default configuration
	this.csrfTokenRepository = cookieCsrfTokenRepository;
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
	registry.addViewController("/login.html").setViewName("login");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

	http.securityContext() //
			.and().exceptionHandling() //
			.and().servletApi() //
			.and().httpBasic() //
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
			//
			.and().logout() //
			/*------*/.logoutSuccessUrl("/") //
			/*------*/.invalidateHttpSession(true)//
			/*------*/.clearAuthentication(true) //
			//
			.and().csrf() //
			/*------*/.csrfTokenRepository(csrfTokenRepository)//
//			/*------*/.ignoringAntMatchers("/actuator/**")	
			//
			.and().requestMatcher(EndpointRequest.toAnyEndpoint()) //
			/*------*/.authorizeRequests() //
			/*------------*/.anyRequest().hasRole("ADMIN")

	;
    }

}
