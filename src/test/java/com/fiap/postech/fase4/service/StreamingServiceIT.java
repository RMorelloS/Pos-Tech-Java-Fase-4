package com.fiap.postech.fase4.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fiap.postech.fase4.config.S3Configuration;
import com.fiap.postech.fase4.config.UserModelGenerator;
import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.model.UserModel;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import com.fiap.postech.fase4.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StreamingServiceIT {

    @Autowired
    private StreamingService streamingService;
    @Autowired
    private StreamingRepository streamingRepository;

    @Test
    public void devePermitirSalvar_LerVideo() throws IOException {


        var videoModel = VideoModelGenerator.generateVideoModel();

        VideoModel video = VideoModelGenerator.salvarVideo(videoModel, streamingService);
        VideoModel savedVideo = streamingService.getVideoById(video.getVideoId());
        assertNotNull(savedVideo);
    }

    @Test
    public void devePermitirObterVideos() throws IOException {


        var videoModel = VideoModelGenerator.generateVideoModel();

        // Salva o vídeo e verifica se foi salvo corretamente
        VideoModel video = streamingService.salvarVideo(videoModel.getTitulo(),
                videoModel.getDescricaoVideo(),
                Mono.just(VideoModelGenerator.generateFilepart()),
                videoModel.getCategorias().get(0),
                videoModel.getAutor());

        var resultado = streamingService.getVideos("Titulo", "Categoria 1", LocalDate.now());
        assertTrue(resultado.size() >= 1);
    }

    @Test
    public List<VideoModel> devePermitirObterVideosUsuario(){
        var resultado = streamingService.getVideosByUser("admin");
        assertTrue(resultado.size() > 1);
        return resultado;

    }

    @Test
    public void devePermitirDeletarVideo() throws IOException {
        var videos = devePermitirObterVideosUsuario();
        var resultado = streamingService.deletarVideo(videos.get(0).getVideoId().toString(), videos.get(0).getAutor());
        assertEquals("Video deletado com sucesso!", resultado);
    }

    @Test
    public void naoDevePermitirDeletarVideoUsuarioNaoAutor() throws IOException {
        var videos = devePermitirObterVideosUsuario();
        var resultado = streamingService.deletarVideo(videos.get(0).getVideoId().toString(), "");
        assertEquals("Usuário sem permissão de excluir vídeo de outros usuários!", resultado);
    }

    @Test
    public void devePermitirObterListaFavoritos() throws IOException {
        var video1 = VideoModelGenerator.generateVideoModel();
        var categorias = video1.getCategorias();
        categorias.add("Categoria 3");
        categorias.add("Categoria 4");
        video1.setCategorias(categorias);
        var video2 = VideoModelGenerator.generateVideoModel();

        var video1AtualizadoUUID = VideoModelGenerator.salvarVideo(video1, streamingService);
        var video2AtualizadoUUID = VideoModelGenerator.salvarVideo(video2, streamingService);

        var resultado = streamingService.obterListaCategorias(new ArrayList<UUID>(Arrays.asList(video1AtualizadoUUID.getVideoId(), video2AtualizadoUUID.getVideoId())));
        assertTrue(resultado.get(0).getKey().equals("Categoria 1") || resultado.get(0).getKey().equals("Categoria 2"));
        assertEquals(2, resultado.get(0).getValue());
        assertTrue(resultado.get(1).getKey().equals("Categoria 1") || resultado.get(1).getKey().equals("Categoria 2"));
        assertEquals(2, resultado.get(1).getValue());
    }

    @Test
    public void devePermitirAtualizarViews() throws IOException {
        var video1 = VideoModelGenerator.generateVideoModel();
        var video1AtualizadoUUID = VideoModelGenerator.salvarVideo(video1, streamingService);
        var visualizacoesVideo1 = video1.getVisualizacoesUsuarios();
        streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        var resultado = streamingService.atualizarViews(video1AtualizadoUUID.getVideoId().toString());
        assertEquals(3, resultado.getVisualizacoesUsuarios());
    }

    @Test
    public void devePermitirAtualizarFavoritos() throws IOException {
        var video1 = VideoModelGenerator.generateVideoModel();
        var video1AtualizadoUUID = VideoModelGenerator.salvarVideo(video1, streamingService);
        var favoritosVideo1 = video1.getQtdeFavoritos();
        streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(),
                StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO);
        streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(),
                StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO);
        var resultado = streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(),
                StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO);
        assertEquals(3, resultado.getQtdeFavoritos());
        streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(),
                StatusFavoritoEnum.StatusFavorito.REMOVER_FAVORITO);
        resultado = streamingService.atualizaFavoritos(video1AtualizadoUUID.getVideoId().toString(),
                StatusFavoritoEnum.StatusFavorito.REMOVER_FAVORITO);
        assertEquals(1, resultado.getQtdeFavoritos());
    }



}
