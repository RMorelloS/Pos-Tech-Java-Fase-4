package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void devePermitirObterIdUsuarioLogado(){

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "admin", List.of(() -> "ROLE_ADMIN")));

        var userId = userService.obterIdUsuarioLogado().block();
        assertEquals("admin", userId);
    }
}
