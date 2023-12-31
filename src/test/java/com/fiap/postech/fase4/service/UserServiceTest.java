package com.fiap.postech.fase4.service;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fiap.postech.fase4.config.UserModelGenerator;
import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers= UserService.class)

public class UserServiceTest {


    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private StreamingService streamingService;

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Test
    public void devePermitirObterUsuarioLogado(){

        when(userRepository.getUserByLogin(any()))
                .thenAnswer(invocation -> {
                    return Mono.just(UserModelGenerator.generateUserModel());
                });

        Mono<UserModel> userMono = userService.obterUsuarioLogado();
        UserModel user = userMono.block();

        assertEquals("user1", user.getUserLogin());
    }

    @Test
    public void devePermitirAdicionarFavorito(){
        var usuarioMockFlux = UserModelGenerator.generateUserModel();


        when(userRepository.getUserByLogin(any()))
                .thenAnswer(invocation -> {
                    return Mono.just(usuarioMockFlux);
                });


        when(userRepository.atualizarUsuario(usuarioMockFlux))
                .thenAnswer(invocation -> {
                    return Mono.just("Atualizado com sucesso!");
                });

        String videoId = UUID.randomUUID().toString();
        Mono<String> resultado = userService.adicionarFavorito(videoId);

        assertEquals("Atualizado com sucesso!", resultado.block());
    }



    @Test
    public void devePermitirRemoverFavorito(){
        var usuarioMockFlux = UserModelGenerator.generateUserModel();

        when(userRepository.getUserByLogin(any()))
                .thenAnswer(invocation -> {
                    return Mono.just(usuarioMockFlux);
                });


        when(userRepository.atualizarUsuario(usuarioMockFlux))
                .thenAnswer(invocation -> {
                    return Mono.just("Atualizado com sucesso!");
                });

        String videoId = UUID.randomUUID().toString();

        var videosFavoritos = usuarioMockFlux.getVideosFavoritos();
        videosFavoritos.add(UUID.fromString(videoId));

        usuarioMockFlux.setVideosFavoritos(videosFavoritos);

        Mono<String> resultado = userService.adicionarFavorito(videoId);

        assertEquals("Atualizado com sucesso!", resultado.block());



    }

    @Test
    public void devePermitirAtualizarFavoritos(){
        ArrayList<UUID> listaFavoritos = new ArrayList<>();
        String videoId = UUID.randomUUID().toString(); // ID do vídeo
        ArrayList<UUID> resultado = userService.atualizaFavoritos(listaFavoritos, videoId);

        // Verifica se o vídeo foi adicionado à lista de favoritos
        assertEquals(1, resultado.size());
        assertEquals(UUID.fromString(videoId), resultado.get(0));
    }

    @Test
    public void devePermitirObterVideosRecomendados(){

        var video1 = VideoModelGenerator.generateVideoModel();
        var video2 = VideoModelGenerator.generateVideoModel();

        ArrayList<VideoModel> listaVideos = new ArrayList<>();
        listaVideos.add(video1);
        listaVideos.add(video2);
        var usuarioMock = UserModelGenerator.generateUserModel();

        var listaFavoritos = new ArrayList<UUID>();
        listaFavoritos.add(UUID.fromString(video1.getVideoId().toString()));
        listaFavoritos.add(UUID.fromString(video1.getVideoId().toString()));

        usuarioMock.setVideosFavoritos(listaFavoritos);

        when(userRepository.getUserByLogin(any()))
                .thenAnswer(invocation -> {
                    return Mono.just(usuarioMock);
                });

        var categoriasOrdenadas = new ArrayList<Map.Entry<String, Integer>>();

        categoriasOrdenadas.add(new AbstractMap.SimpleEntry<>("Categoria 1", 1));
        categoriasOrdenadas.add(new AbstractMap.SimpleEntry<>("Categoria 2", 1));


        when(streamingService.obterListaCategorias(any(ArrayList.class))).thenReturn(categoriasOrdenadas);


        when(streamingService.obterVideosPorCategorias(categoriasOrdenadas))
                .thenAnswer(invocation -> {
                    return listaVideos;
                });

        var resultado = userService.obterVideosRecomendados().block();

        assertEquals(video1.getVideoId(), resultado.get(0).getVideoId());

        assertEquals(video2.getVideoId(), resultado.get(1).getVideoId());
    }

    @Test
    public void devePermitirContarFavoritos(){
        UUID videoId = UUID.randomUUID();

        List<String> categorias = new ArrayList<>();
        categorias.add("Ação");
        categorias.add("Aventura");

        String descricao = "Descrição do vídeo...";

        VideoModel video = VideoModelGenerator.generateVideoModel();

        List<VideoModel> listaVideos = new ArrayList<VideoModel>();
        listaVideos.add(video);
        var resultado = userService.contaFavoritos(listaVideos);

        assertEquals(listaVideos.size(), resultado);
    }

    @Test
    public void devePermitirCriarConta(){

        var usuario = UserModelGenerator.generateUserModel();
        when(userRepository.criarUsuario(any(UserModel.class))).thenReturn("Criado com sucesso!");

        var resultado = userService.criarConta(usuario.getUserLogin(), usuario.getUserKey(), usuario.getEmail()).block();

        assertEquals("Criado com sucesso!", resultado);
    }

    @Test
    public void devePermitirObterEstatisticas(){
        var usuarioMockFlux = UserModelGenerator.generateUserModel();

        when(userRepository.getUserByLogin(any()))
                .thenAnswer(invocation -> {
                    return Mono.just(usuarioMockFlux);
                });

        var video1 = VideoModelGenerator.generateVideoModel();
        var video2 = VideoModelGenerator.generateVideoModel();

        ArrayList<VideoModel> listaVideos = new ArrayList<>();
        listaVideos.add(video1);
        listaVideos.add(video2);


        when(userRepository.obterVideosUsuario(any(UserModel.class))).thenReturn(listaVideos);

        var resultado = userService.obterEstatisticas().block();

        assertEquals(listaVideos.stream().count(), resultado.getQtdeVideos());
        assertEquals(2, resultado.getQtdeVideos());
        assertEquals(2, resultado.getQtdeVideosFavoritados());
        assertEquals((video1.getVisualizacoesUsuarios() + video2.getVisualizacoesUsuarios())/2, resultado.getMediaVisualizacoes());
    }


}