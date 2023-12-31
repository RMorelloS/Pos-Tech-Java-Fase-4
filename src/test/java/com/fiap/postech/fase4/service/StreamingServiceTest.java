package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers= StreamingService.class)

public  class StreamingServiceTest {


    @MockBean
    private VideoUploadService videoUploadService;

    @MockBean
    private StreamingRepository streamingRepository;

    @Autowired
    private StreamingService streamingService;

    @Test
    public void devePermitirSalvarVideo() {
        String titulo = "titulo1";
        String descricao = "descricao1";
        String categorias = "Categoria1;Categoria2";
        String autor = "autor1";
        FilePart filePart = mock(FilePart.class);

        when(videoUploadService.uploadObject(any(), any())).thenReturn(Mono.just("Carregado com sucesso"));
        when(streamingRepository.salvarVideo(any(VideoModel.class))).thenReturn(new VideoModel());
        ClassPathResource resource = new ClassPathResource("videos/sample.mp4");

        VideoModel resultado = streamingService.salvarVideo(titulo, descricao, Mono.just(filePart), categorias, autor);

        verify(videoUploadService, times(1)).uploadObject(any(), any());
        verify(streamingRepository, times(1)).salvarVideo(any(VideoModel.class));
        assertEquals("titulo1", resultado.getTitulo());
    }

    @Test
    public void devePermitirObterVideoPorId() {
        String videoId = "6d82dcff-c21e-441c-8ce2-60e8f1d83fd7";
        when(streamingRepository.getVideoById(any(UUID.class))).thenReturn(new VideoModel());
        VideoModel resultado = streamingService.getVideoById(UUID.fromString(videoId));
        verify(streamingRepository, times(1)).getVideoById(any(UUID.class));
        assertEquals(new VideoModel(), resultado);
    }

    @Test
    public void devePermitirObterListaCategorias() {
        var randomUUID1 = UUID.randomUUID();
        var randomUUID2 = UUID.randomUUID();

        ArrayList<UUID> listaFavoritos = new ArrayList<>();
        listaFavoritos.add(randomUUID1);
        listaFavoritos.add(randomUUID2);
        VideoModel video1 = new VideoModel();
        video1.setCategorias(Arrays.asList("A", "B"));

        VideoModel video2 = new VideoModel();
        video2.setCategorias(Arrays.asList("B", "C"));


        Mockito.when(streamingService.getVideoById(randomUUID1))
                .thenReturn(video1);

        Mockito.when(streamingService.getVideoById(randomUUID2))
                .thenReturn(video2);

        List<Map.Entry<String, Integer>> categoriasOrdenadas = streamingService.obterListaCategorias(listaFavoritos);

        assertEquals(2, categoriasOrdenadas.size());
        assertEquals("B", categoriasOrdenadas.get(0).getKey());
        assertEquals(2, categoriasOrdenadas.get(0).getValue());

        assertEquals("A", categoriasOrdenadas.get(1).getKey());
        assertEquals(1, categoriasOrdenadas.get(1).getValue());
    }


    @Test
    public void devePermitirObterVideosFiltradoCategoriaTitulo() {

        var videoModel = VideoModelGenerator.generateVideoModel();
        var videoModelList = new ArrayList<VideoModel>();
        videoModelList.add(videoModel);
        when(streamingRepository.getVideos(Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any()))
                .thenReturn(videoModelList);

        List<VideoModel> resultado =
                streamingService.getVideos("Titulo", "Categoria", null);
        verify(streamingRepository, times(1)).getVideos("Titulo", "Categoria", null);
        assertEquals(videoModelList, resultado);

    }

    @Test
    public void devePermitirObterVideosUsuario() {
        var videoModel = VideoModelGenerator.generateVideoModel();
        var videoModelList = new ArrayList<VideoModel>();
        videoModelList.add(videoModel);
        when(streamingRepository.getVideosByUser(Mockito.any(String.class)))
                .thenReturn(videoModelList);

        List<VideoModel> resultado =
                streamingService.getVideosByUser("admin");
        verify(streamingRepository, times(1)).getVideosByUser("admin");
        assertEquals(videoModelList, resultado);
    }


    @Test
    public void devePermitirDeletarVideo() {
        var videoId = UUID.randomUUID();
        when(streamingRepository.delete(Mockito.any(UUID.class), Mockito.any(String.class)))
                .thenReturn("deletado com sucesso!");
        String resultado =
                streamingService.deletarVideo(videoId.toString(), "admin");
        verify(streamingRepository, times(1)).delete(videoId, "admin");
        assertEquals("deletado com sucesso!", resultado);
    }


    @Test
    public void devePermitirObterVideosPorCategorias() {
        var categorias = new ArrayList<Map.Entry<String, Integer>>();
        Map.Entry<String, Integer> novaEntrada = new AbstractMap.SimpleEntry<>("chave", 10);
        categorias.add(novaEntrada);

        var listaVideos = new ArrayList<VideoModel>();
        listaVideos.add(VideoModelGenerator.generateVideoModel());

        when(streamingRepository.obterVideosPorCategorias(categorias))
                .thenReturn(listaVideos);

        List<VideoModel> resultado =
                streamingService.obterVideosPorCategorias(categorias);
        verify(streamingRepository, times(1)).obterVideosPorCategorias(categorias);
        assertEquals(listaVideos, resultado);

    }

    @Test
    public void devePermitirContabilizarQtdeVisualizacoes() {
        var video = VideoModelGenerator.generateVideoModel();
        var visualizacoes = video.getVisualizacoesUsuarios();
        when(streamingRepository.getVideoById(video.getVideoId()))
                .thenReturn(video);
        var resultado = streamingService.atualizarViews(video.getVideoId().toString());
        verify(streamingRepository, times(1)).salvarVideo(any());
        assertEquals(visualizacoes + 1, resultado.getVisualizacoesUsuarios());
    }

    @Test
    public void devePermitirAtualizarQtdeFavoritos() {
        var video = VideoModelGenerator.generateVideoModel();
        var favoritos = video.getQtdeFavoritos();
        when(streamingRepository.getVideoById(video.getVideoId()))
                .thenReturn(video);
        var resultado = streamingService.atualizaFavoritos(video.getVideoId().toString(), StatusFavoritoEnum.StatusFavorito.ADICIONAR_FAVORITO);
        verify(streamingRepository, times(1)).salvarVideo(any());
        assertEquals(favoritos + 1, resultado.getQtdeFavoritos());

        resultado = streamingService.atualizaFavoritos(video.getVideoId().toString(), StatusFavoritoEnum.StatusFavorito.REMOVER_FAVORITO);
        assertEquals(favoritos, resultado.getQtdeFavoritos());


    }
}