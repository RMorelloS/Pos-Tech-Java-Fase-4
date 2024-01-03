package com.fiap.postech.fase4.controller;

import com.fiap.postech.fase4.config.UserModelGenerator;
import com.fiap.postech.fase4.service.StreamingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StreamingService streamingService;

    @Test
    public void devePermitirAdicionarFavorito() {

        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("videoId", videos.get(0).getVideoId().toString());
        webTestClient.post()
                .uri("/usuario/adicionarFavorito")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/listarVideos");
    }

    @Test
    public void devePermitirObterTelaRecomendados() {

        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/usuario/recomendados")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertTrue("Atributo lista de videos recomendados não identificado no model",
                            responseBody.contains("videos"));
                });
    }

    @Test
    public void devePermitirCriarConta(){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("login", "usuarioControllerITTeste");
        formData.add("chave", "usuarioControllerITTeste");
        formData.add("email", "usuarioControllerITTeste@email.com");
        webTestClient.post()
                .uri("/usuario/criarConta")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/login");
    }

    @Test
    public void devePermitirAbrirTelaRegistrarUsuario(){

        webTestClient.get()
                .uri("/usuario/registrarUsuario")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }

    @Test
    public void devePermitirObterEstatisticas(){
        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/usuario/estatisticas")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertTrue("Atributo lista de videos não identificado no model",
                            responseBody.contains("videos"));
                    assertTrue("Atributo estatisticas não identificado no model",
                            responseBody.contains("estatisticas"));
                });
    }
}
