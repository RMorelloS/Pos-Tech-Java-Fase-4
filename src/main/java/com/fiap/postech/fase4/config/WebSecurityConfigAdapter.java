package com.fiap.postech.fase4.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerRedirectStrategy;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfigAdapter{


    @Autowired
    private DynamoDBUserDetailsService userDetailsService;

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationProvider() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        URI returnURI = new URI("/listarVideos");
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login").permitAll()
                        .pathMatchers("/usuario/registrarUsuario").permitAll()
                        .pathMatchers("/usuario/criarConta").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                            ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

                            if (response != null && response.bufferFactory() != null) {
                                URI returnUrl = URI.create("/listarVideos");
                                HttpHeaders headers = response.getHeaders();
                                headers.setLocation(returnUrl);
                                response.setStatusCode(HttpStatus.SEE_OTHER);

                                return response.setComplete();
                            } else {
                                return Mono.error(new IllegalStateException("Response or buffer factory is null."));
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((webFilterExchange, authentication) ->
                                Mono.fromRunnable(() ->
                                        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.OK)
                                )
                        )
                )
                .csrf((csrf) -> csrf.disable())
                .build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }





    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new DynamoDBUserDetailsService();
    }
}