package org.rpis5.chapters.chapter_09;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@EnableReactiveMethodSecurity
public class TestSecurityConfiguration {

	@Bean
	@Primary
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		return http
					.authorizeExchange()
					.anyExchange().permitAll()
		            .and()
		            .formLogin()
					.and()
					.csrf().disable()
					.build();
	}
}
