package com.fiap.postech.fase4.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers= UserRepository.class)
public class UserRepositoryTest {

    @MockBean
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void devePermitirObterUsuarioPorLogin(){
        var usuario = UserModelGenerator.generateUserModel();

        List<UserModel> listaUsuarios = Collections.singletonList(usuario);
        when(dynamoDBMapper.scan(eq(UserModel.class), any(DynamoDBScanExpression.class))).thenAnswer(invocation -> listaUsuarios);

        var saida = userRepository.getUserByLogin(Mono.just(usuario.getUserLogin()));

        saida.doOnNext(resultado -> {
            assertNotNull(resultado);
            assertEquals(usuario.getUserLogin(), resultado.getUserLogin());
        }).block();
    }

    @Test
    public void devePermitirCriarUsuario(){
        var usuario = UserModelGenerator.generateUserModel();
        doNothing().when(dynamoDBMapper).save(usuario);
        var resultado = userRepository.criarUsuario(usuario);
        assertEquals("Criado com sucesso", resultado);


    }

    @Test
    public void devePermitirObterVideosUsuario(){
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

        var resultado = userRepository.obterVideosUsuario(UserModelGenerator.generateUserModel());
        assertEquals(video1, resultado.get(0));
        assertEquals(video2, resultado.get(1));
        assertEquals(2, resultado.stream().count());

    }
}
