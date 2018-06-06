package org.rpis5.chapters.chapter_09;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails userAdmin = User.withDefaultPasswordEncoder()
		                            .username("admin")
		                            .password("admin")
		                            .roles("ADMIN")
		                            .build();
		UserDetails userUser = User.withDefaultPasswordEncoder()
		                            .username("user")
		                            .password("user")
		                            .roles("USER")
		                            .build();
		return new MapReactiveUserDetailsService(userAdmin, userUser);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				.authorizeExchange()
				.anyExchange().permitAll()
				.and()
				.formLogin();
		return http.build();
	}
}
