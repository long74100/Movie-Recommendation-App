package edu.northeastern.cs4500.configurations;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;
	private final String userQuery = "select email, password, active from user where email=?";
	private final String roleQuery = "select email, role.role from user join role on user.role = role.role_id  where email=?";
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.
			jdbcAuthentication()
				.usersByUsernameQuery(userQuery)
				.authoritiesByUsernameQuery(roleQuery)
				.dataSource(dataSource)
				.passwordEncoder(bCryptPasswordEncoder);

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			.authorizeRequests()
				.antMatchers("/", "/login", "/test", "/registration", "/search/**", "/movie/**", "/writeReview/**","/view/**").permitAll()
				.antMatchers("/admin/**")
				.hasAnyRole("ADMIN")
				.anyRequest()
				.authenticated()
				.and()
				.csrf()
				.disable()
			.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error=true")
				.defaultSuccessUrl("/")
				.usernameParameter("email")
				.passwordParameter("password")
				.and()
			.logout()
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.and()
			.exceptionHandling()
				.accessDeniedPage("/access-denied");
		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	       .ignoring()
	       .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}
	
	

}
