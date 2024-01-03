package com.fiap.postech.fase4.controller;


import com.fiap.postech.fase4.config.UserModelGenerator;
import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.controller.StreamingController;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.service.StreamingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
public class StreamingControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StreamingService streamingService;
    @Test
    public void devePermitirAbrirVideo(){

        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("videoId", videos.get(0).getVideoId().toString());
        webTestClient.post()
                .uri("/abrirVideo")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/mostrarVideo/" + videos.get(0).getVideoId());
    }

    @Test
    public void devePermitirAbrirTelaMostrarVideo(){
        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/mostrarVideo/" + videos.get(1).getVideoId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertTrue("Atributo id do vídeo não identificado no model",
                            responseBody.contains("video"));
                });
    }

    @Test
    public void devePermitirListarVideos() {
        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/listarVideos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertTrue("Atributo lista de vídeos não identificado no model",
                            responseBody.contains("videos"));
                    assertTrue("Atributo titulo do vídeo não identificado no model",
                            responseBody.contains("tituloVideo"));
                    assertTrue("Atributo categoria do video não identificado no model",
                            responseBody.contains("categoriaVideo"));
                    assertTrue("Atributo data de publicação do video não identificado no model",
                            responseBody.contains("dataPublicacaoVideo"));
                });
    }

    @Test
    public void devePermitirDeletarVideos(){

        var usuario = UserModelGenerator.generateUserModel();
        var videos = streamingService.getVideosByUser("admin");
        assertTrue("Sem vídeos para teste!", videos.stream().count() > 0);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("videoId", videos.get(0).getVideoId().toString());
        webTestClient.post()
                .uri("/deletarVideos")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    public void devePermitirAbrirTelaCarregarVideos(){
        var usuario = UserModelGenerator.generateUserModel();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/carregarVideos")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }

    @Test
    public void devePermitirAbrirTelaLogin(){
        var usuario = UserModelGenerator.generateUserModel();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        webTestClient.get()
                .uri("/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);
    }

}
