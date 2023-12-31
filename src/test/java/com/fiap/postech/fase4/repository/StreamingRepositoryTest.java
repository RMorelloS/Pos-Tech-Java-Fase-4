package com.fiap.postech.fase4.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fiap.postech.fase4.config.VideoModelGenerator;
import com.fiap.postech.fase4.model.VideoModel;
import com.fiap.postech.fase4.repository.StreamingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@WebFluxTest(controllers = StreamingRepository.class)
public class StreamingRepositoryTest {

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private StreamingRepository streamingRepository;
    @Test
    public void devePermitirSalvarVideo(){
        var video = VideoModelGenerator.generateVideoModel();

        doNothing().when(dynamoDBMapper).save(video);
        var resultado = streamingRepository.salvarVideo(video);
        assertEquals(video, resultado);
    }

    @Test
    public void devePermitirObterVideoPorId(){
        var video = VideoModelGenerator.generateVideoModel();

        when(dynamoDBMapper.load(VideoModel.class, video.getVideoId())).thenReturn(video);
        var resultado = streamingRepository.getVideoById(video.getVideoId());
        assertEquals(video, resultado);
    }

    @Test
    public void devePermitirDeletarVideo(){
        var video = VideoModelGenerator.generateVideoModel();
        video.setAutor("admin");

        when(dynamoDBMapper.load(VideoModel.class, video.getVideoId())).thenReturn(video);
        doNothing().when(dynamoDBMapper).delete(video);

        var resultado = streamingRepository.delete(video.getVideoId(), "admin");
        assertEquals("Video deletado com sucesso!", resultado);
    }

    @Test
    public void naoDevePermitirDeletarVideoQuandoAutorDiferenteUsuarioLogado(){
        var video = VideoModelGenerator.generateVideoModel();
        video.setAutor("user1");

        when(dynamoDBMapper.load(VideoModel.class, video.getVideoId())).thenReturn(video);
        doNothing().when(dynamoDBMapper).delete(video);

        var resultado = streamingRepository.delete(video.getVideoId(), "admin");
        assertNotEquals("Video deletado com sucesso!", resultado);
    }

    @Test
    public void devePermitirObterVideos(){
        var video1 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        var video2 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        ArrayList<VideoModel> listaVideos = new ArrayList<>();
        listaVideos.add(video1);
        listaVideos.add(video2);

        PaginatedScanList<VideoModel> paginatedScanList = mock(PaginatedScanList.class);
        when(paginatedScanList.iterator()).thenReturn(listaVideos.iterator());
        when(paginatedScanList.stream()).thenReturn(listaVideos.stream());
        when(paginatedScanList.size()).thenReturn(listaVideos.size());
        when(paginatedScanList.get(0)).thenReturn(video1);
        when(paginatedScanList.get(1)).thenReturn(video2);

        when(dynamoDBMapper.scan(eq(VideoModel.class), any(DynamoDBScanExpression.class))).thenReturn(paginatedScanList);

        var resultado = streamingRepository.getVideos("titulo", "categoria", LocalDate.now());

        assertEquals(2, resultado.size());

        assertEquals(listaVideos.get(0), resultado.get(0));
        assertEquals(listaVideos.get(1), resultado.get(1));
    }

    @Test
    public void devePermitirObterVideosDoUsuario(){
        var video1 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        var video2 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        ArrayList<VideoModel> listaVideos = new ArrayList<>();
        listaVideos.add(video1);
        listaVideos.add(video2);

        PaginatedScanList<VideoModel> paginatedScanList = mock(PaginatedScanList.class);
        when(paginatedScanList.iterator()).thenReturn(listaVideos.iterator());
        when(paginatedScanList.stream()).thenReturn(listaVideos.stream());
        when(paginatedScanList.size()).thenReturn(listaVideos.size());
        when(paginatedScanList.get(0)).thenReturn(video1);
        when(paginatedScanList.get(1)).thenReturn(video2);

        when(dynamoDBMapper.scan(eq(VideoModel.class), any(DynamoDBScanExpression.class))).thenReturn(paginatedScanList);


        var resultado = streamingRepository.getVideosByUser("user1");

        assertEquals(2, resultado.size());
    }

    @Test
    public void devePermitirObterVideosPorCategoria(){


        var video1 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        var video2 = VideoModelGenerator.generateVideoModel();
        video1.setAutor("user1");

        ArrayList<VideoModel> listaVideos = new ArrayList<>();
        listaVideos.add(video1);
        listaVideos.add(video2);

        PaginatedScanList<VideoModel> paginatedScanList = mock(PaginatedScanList.class);
        when(paginatedScanList.iterator()).thenReturn(listaVideos.iterator());
        when(paginatedScanList.stream()).thenReturn(listaVideos.stream());
        when(paginatedScanList.size()).thenReturn(listaVideos.size());
        when(paginatedScanList.get(0)).thenReturn(video1);
        when(paginatedScanList.get(1)).thenReturn(video2);

        var categoriasOrdenadas = new ArrayList<Map.Entry<String, Integer>>();

        categoriasOrdenadas.add(new AbstractMap.SimpleEntry<>("Categoria 1", 1));
        categoriasOrdenadas.add(new AbstractMap.SimpleEntry<>("Categoria 2", 1));

        when(dynamoDBMapper.scan(eq(VideoModel.class), any(DynamoDBScanExpression.class))).thenReturn(paginatedScanList);

        var resultado = streamingRepository.obterVideosPorCategorias(categoriasOrdenadas);
        assertEquals(video1, resultado.get(0));
        assertEquals(video2, resultado.get(1));
        assertEquals(2, resultado.size());

    }

}
