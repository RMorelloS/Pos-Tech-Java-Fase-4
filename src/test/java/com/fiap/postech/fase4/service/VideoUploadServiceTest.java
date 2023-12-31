package com.fiap.postech.fase4.service;


import com.fiap.postech.fase4.config.S3Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers= VideoUploadService.class)

public class VideoUploadServiceTest {

    @MockBean
    private S3Client s3Client;
    @MockBean
    private S3Configuration s3Configuration;
    @Autowired
    private VideoUploadService videoUploadService;


    @Test
    public void devePermitirDeletarVideo(){

        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());

        when(s3Configuration.buildS3Client()).thenReturn(s3Client);

        String result = videoUploadService.delete(UUID.randomUUID().toString());

        assertEquals("Deletado com sucesso!", result);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    public void devePermitirCarregarObjeto(){
        FilePart filePart = mock(FilePart.class);

        DataBuffer buffer = mock(DataBuffer.class);
        when(buffer.asInputStream()).thenReturn(new ByteArrayInputStream("teste".getBytes()));

        when(filePart.content()).thenReturn(Flux.just(buffer));

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        var resultado = videoUploadService.uploadObject(Mono.just(filePart), UUID.randomUUID());
        assertEquals("Carregado com sucesso!", resultado.block());
    }

}