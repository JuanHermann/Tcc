package com.github.adminfaces.starter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.adminfaces.starter.service.UsuarioService;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UsuarioService service;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.formLogin()
				.loginPage("/login.xhtml")
				.failureUrl("/login.xhtml?error=bad-credentials")
				.permitAll()
			.and()
			.logout()
				.logoutSuccessUrl("/login.xhtml")
				.permitAll()
			.and()
			.authorizeRequests()
				.antMatchers("/usuario.xhtml").hasRole("ADMIN")
				.antMatchers("/public/**").permitAll()
				.antMatchers("/cadastro.xhtml").permitAll()
				.antMatchers("/senha.xhtml").permitAll()
				.antMatchers("/javax.faces.resource/**").permitAll()
					.anyRequest().authenticated()
			.and().csrf().disable();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
	}

}
















