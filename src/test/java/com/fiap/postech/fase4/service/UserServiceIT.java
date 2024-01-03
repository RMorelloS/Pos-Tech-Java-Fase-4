package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.config.UserModelGenerator;
import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.repository.UserRepository;
import com.fiap.postech.fase4.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserServiceIT {

    @Autowired
    private UserService userService;


    @Autowired
    private StreamingService streamingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void devePermitirObterIdUsuarioLogado(){
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "admin", List.of(() -> "ROLE_ADMIN")));

        var userId = userService.obterIdUsuarioLogado().block();
        assertEquals("admin", userId);
    }

    @Test
    public void devePermitirAdicionarFavorito() throws IOException {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "admin", List.of(() -> "ROLE_ADMIN")));

        var video = VideoModelGenerator.salvarVideo(VideoModelGenerator.generateVideoModel(), streamingService);

        var resultado = userService.adicionarFavorito(video.getVideoId().toString());
        assertEquals("Atualizado com sucesso!", resultado.block());

        var usuario = userService.obterUsuarioLogado().block();
        assertTrue("ID do vídeo não encontrado nos favoritos do usuario",
                usuario.getVideosFavoritos().contains(video.getVideoId()));
    }

    @Test
    public void devePermitirCriarConta(){
        var usuario = UserModelGenerator.generateUserModel();
        var resultado = userService.criarConta(usuario.getUserLogin(), usuario.getUserKey(), usuario.getEmail());
        assertEquals("Criado com sucesso", resultado.block());
        assertNotNull("Usuario não encontrado!", userRepository.getUserByLogin(Mono.just("teste")));
    }

    @Test
    public void devePermitirObterVideosRecomendados() throws IOException {

        var usuario = UserModelGenerator.generateUserModel();
        var resultado = userService.criarConta(usuario.getUserLogin(), usuario.getUserKey(), usuario.getEmail());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));

        var video1 = VideoModelGenerator.generateVideoModel();
        var video1AtualizadoUUID = VideoModelGenerator.salvarVideo(video1, streamingService);

        userService.adicionarFavorito(video1AtualizadoUUID.getVideoId().toString()).block();

        var videosRecomendados = userService.obterVideosRecomendados().block();

        Assertions.assertTrue(videosRecomendados.size() > 1);

        var videosCategoria1 = streamingService.getVideos("", video1.getCategorias().get(0), null);
        var videosCategoria2 = streamingService.getVideos("", video1.getCategorias().get(1), null);

        assertEquals(videosCategoria1.size() + videosCategoria2.size(), videosRecomendados.size());
    }
    @Test
    public void devePermitirCalcularMediaVisualizacoes() throws IOException {

        var usuario = UserModelGenerator.generateUserModel();
        var resultado = userService.criarConta(usuario.getUserLogin(), usuario.getUserKey(), usuario.getEmail());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getUserLogin(), usuario.getUserKey(), List.of(() -> "ROLE_USER")));


        var video1 = VideoModelGenerator.generateVideoModel();
        video1.setAutor(usuario.getUserLogin());
        var video1AtualizadoUUID = VideoModelGenerator.salvarVideo(video1, streamingService);


        var video2 = VideoModelGenerator.generateVideoModel();
        video2.setAutor(usuario.getUserLogin());
        var video2AtualizadoUUID = VideoModelGenerator.salvarVideo(video2, streamingService);

        streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video2AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video2AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video2AtualizadoUUID.getVideoId().toString());

        streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(), StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO);


        var estatisticas = userService.obterEstatisticas().block();

        assertEquals(streamingService.getVideosByUser(usuario.getUserLogin()).stream().count(), estatisticas.getQtdeVideos());


    }
}
