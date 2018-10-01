package org.rpis5.chapters.chapter_06.security;

import java.util.regex.Pattern;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

    private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");
    private static final String NOOP_PASSWORD_PREFIX = "{noop}";

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(
        ObjectProvider<PasswordEncoder> passwordEncoder
    ) {

        return new MapReactiveUserDetailsService(
            User.withUsername("user")
                .password("user")
                .passwordEncoder(p -> getOrDeducePassword(p, passwordEncoder.getIfAvailable()))
                .roles("USER")
                .build(),
            User.withUsername("admin")
                .password("admin")
                .passwordEncoder(p -> getOrDeducePassword(p, passwordEncoder.getIfAvailable()))
                .roles("USER", "ADMIN")
                .build()
        );
    }

    @Bean
    public SecurityWebFilterChain securityFilterChainConfigurer(ServerHttpSecurity httpSecurity) {
        return httpSecurity

            .authorizeExchange()
                .anyExchange().permitAll()
            .and()

            .httpBasic()
            .and()

            .formLogin()
            .and()

            .build();
    }

    private String getOrDeducePassword(
        String password,
        PasswordEncoder encoder
    ) {
        if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
            return password;
        }
        return NOOP_PASSWORD_PREFIX + password;
    }
}
