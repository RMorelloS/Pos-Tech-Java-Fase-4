package com.fiap.postech.fase4.controller;


import com.fiap.postech.fase4.config.MultiPartBodyGenerator;
import com.fiap.postech.fase4.controller.UserController;
import com.fiap.postech.fase4.model.EstatisticasUsuarioModel;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.UserRepository;
import com.fiap.postech.fase4.service.StreamingService;
import com.fiap.postech.fase4.service.UserService;
import com.fiap.postech.fase4.service.VideoUploadService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@WebFluxTest(controllers= UserController.class)
@ComponentScan(basePackages = "com.fiap.postech.fase4.testConfig")

public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private VideoUploadService videoUploadService;
    @MockBean
    private StreamingService streamingService;

    @Test
    public void devePermitirAdicionarFavorito(){

        Mockito.when(userService.obterUsuarioLogado())
                .thenReturn(Mono.just(new UserModel("admin",
                        "admin", "ADMIN",
                        "admin@admin.com", null)));

        Mockito.when(userService.adicionarFavorito(Mockito.anyString()))
                .thenReturn(Mono.just("redirect:/listarVideos"));


        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("videoId", "6d82dcff-c21e-441c-8ce2-60e8f1d83fd7");


        MultiValueMap<String, HttpEntity<?>> multipartBody = MultiPartBodyGenerator.createMultipartBody(builder);

        webTestClient
                .post().uri("/usuario/adicionarFavorito")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/listarVideos");
    }



    @Test
    public void devePermitirMostrarVideosRecomendados(){

        var listaVideos = new ArrayList<VideoModel>();
        Mockito.when(userService.obterVideosRecomendados())
                .thenReturn(Mono.just(new ArrayList<VideoModel>()));


        webTestClient
                .get().uri("/usuario/recomendados")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    Assertions.assertThat(responseBody).contains("videos");
                });
    }



    @Test
    public void devePermitirCriarConta(){


        Mockito.when(userService.criarConta(Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Mono.just("redirect:/login"));


        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("login", "usuario1");
        builder.part("chave", "chave1");
        builder.part("email", "email@email.com");


        MultiValueMap<String, HttpEntity<?>> multipartBody = MultiPartBodyGenerator.createMultipartBody(builder);

        webTestClient
                .post().uri("/usuario/criarConta")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartBody))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login");
    }

    @Test
    public void devePermitirMostrarTelaRegistrarUsuário(){

        webTestClient
                .get().uri("/usuario/registrarUsuario" )
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> body.contains("<title>Youvideos - Cadastro</title>"));

    }


    @Test
    public void devePermitirMostrarTelaEstatisticas(){


        Mockito.when(userService.obterEstatisticas())
                .thenReturn(Mono.just(new EstatisticasUsuarioModel()));


        Mockito.when(userService.obterUsuarioLogado())
                .thenReturn(Mono.just(new UserModel("admin", "admin",
                        "ADMIN", "admin@admin", null)));


        Mockito.when(userService.obterIdUsuarioLogado())
                .thenReturn(Mono.just("admin"));

        webTestClient
                .get().uri("/usuario/estatisticas")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    Assertions.assertThat(responseBody).contains("videos");
                    Assertions.assertThat(responseBody).contains("estatisticas");
                })
                .value(body -> body.contains("<title>Youvideos - Estatísticas</title>"));

    }
}
