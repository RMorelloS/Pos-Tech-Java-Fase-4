package com.fiap.postech.fase4.service;

import com.fiap.postech.fase4.config.VideoModelGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
public class VideoUploadServiceIT {
    @Autowired
    private VideoUploadService videoUploadService;
    @Autowired
    private StreamingService streamingService;
    @Test
    public void devePermitirObterURLVideoS3() throws IOException {
        var videoSalvo = VideoModelGenerator.salvarVideo(VideoModelGenerator.generateVideoModel(), streamingService);
        var resultado = videoUploadService.getVideoS3URL(videoSalvo.getVideoId().toString());
        assertNotNull("URL do vídeo não pode ser nula!", resultado);
    }
}
